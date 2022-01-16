package com.trader.listener;

public interface PriceListener {

    void priceUpdate(String security, double price);
}
