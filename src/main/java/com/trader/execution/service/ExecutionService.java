package com.trader.execution.service;

public interface ExecutionService {

    void buy(String security, double price, int volume);

    void sell(String security, double price, int volume);
}
