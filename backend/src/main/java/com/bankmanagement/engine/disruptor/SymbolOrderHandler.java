package com.bankmanagement.engine.disruptor;

import com.lmax.disruptor.EventHandler;
import java.math.BigDecimal;
import java.util.function.BiConsumer;

/**
 * Per-symbol Disruptor consumer. Serializes all market-price callbacks for one symbol
 * onto a single dedicated thread, eliminating cross-symbol lock contention on the
 * resting order book.
 */
public final class SymbolOrderHandler implements EventHandler<MarketPriceEvent> {

    private final BiConsumer<String, BigDecimal> onPrice;

    public SymbolOrderHandler(BiConsumer<String, BigDecimal> onPrice) {
        this.onPrice = onPrice;
    }

    @Override
    public void onEvent(MarketPriceEvent event, long sequence, boolean endOfBatch) {
        onPrice.accept(event.symbol, event.price);
    }
}
