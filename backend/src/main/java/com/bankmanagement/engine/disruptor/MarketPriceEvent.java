package com.bankmanagement.engine.disruptor;

import com.lmax.disruptor.EventFactory;
import java.math.BigDecimal;

/** Mutable event slot reused by each symbol's ring buffer — never hold a reference across publish cycles. */
public final class MarketPriceEvent {

    public String symbol;
    public BigDecimal price;

    public static final EventFactory<MarketPriceEvent> FACTORY = MarketPriceEvent::new;
}
