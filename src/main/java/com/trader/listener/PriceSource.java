package com.trader.listener;

public interface PriceSource {

    void addPriceListener(PriceListener listener);

    void removePriceListener(PriceListener listener);
}
