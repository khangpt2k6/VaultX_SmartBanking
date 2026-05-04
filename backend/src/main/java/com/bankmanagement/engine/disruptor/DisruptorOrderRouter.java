package com.bankmanagement.engine.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Routes market-price events to per-symbol LMAX Disruptor ring buffers.
 *
 * Each symbol gets its own Disruptor (and thus its own writer thread), so AAPL tick
 * processing never blocks MSFT tick processing. Publishing from MarketDataService's
 * parallelStream is safe because ProducerType.MULTI is used.
 *
 * Ring buffer size of 1024 is intentionally larger than the fastest tick rate so
 * producers never stall waiting for the consumer to drain.
 */
@Component
public class DisruptorOrderRouter {

    private static final Logger log = LoggerFactory.getLogger(DisruptorOrderRouter.class);
    private static final int RING_BUFFER_SIZE = 1024;

    private final Map<String, Disruptor<MarketPriceEvent>> disruptors = new ConcurrentHashMap<>();
    private final Map<String, RingBuffer<MarketPriceEvent>> ringBuffers = new ConcurrentHashMap<>();

    /**
     * Create and start a Disruptor for {@code symbol}, wiring {@code handler} as the sole consumer.
     * No-op if the symbol is already registered (idempotent).
     */
    public void registerSymbol(String symbol, BiConsumer<String, BigDecimal> handler) {
        if (ringBuffers.containsKey(symbol)) {
            return;
        }

        Disruptor<MarketPriceEvent> disruptor = new Disruptor<>(
            MarketPriceEvent.FACTORY,
            RING_BUFFER_SIZE,
            r -> {
                Thread t = new Thread(r, "disruptor-" + symbol);
                t.setDaemon(true);
                return t;
            },
            ProducerType.MULTI,
            new SleepingWaitStrategy()
        );

        disruptor.handleEventsWith(new SymbolOrderHandler(handler));
        disruptor.start();

        disruptors.put(symbol, disruptor);
        ringBuffers.put(symbol, disruptor.getRingBuffer());
        log.debug("Disruptor registered for {}", symbol);
    }

    /**
     * Publish a price update to the symbol's ring buffer.
     * Lock-free on the happy path — uses CAS sequence claim.
     * Falls back to {@code fallback} if the symbol has no registered ring yet (startup race).
     */
    public void publish(String symbol, BigDecimal price, BiConsumer<String, BigDecimal> fallback) {
        RingBuffer<MarketPriceEvent> rb = ringBuffers.get(symbol);
        if (rb == null) {
            fallback.accept(symbol, price);
            return;
        }
        long seq = rb.next();
        try {
            MarketPriceEvent event = rb.get(seq);
            event.symbol = symbol;
            event.price = price;
        } finally {
            rb.publish(seq);
        }
    }

    @PreDestroy
    public void shutdown() {
        disruptors.values().forEach(d -> {
            try {
                d.shutdown(1, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
        });
        log.info("All symbol Disruptors shut down");
    }
}
