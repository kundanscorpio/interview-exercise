package com.trader.execution.strategy;

import com.trader.execution.service.ExecutionService;
import com.trader.execution.service.ExecutionServiceImpl;
import com.trader.listener.BuyPriceListener;
import com.trader.listener.PriceSourceImpl;
import com.trader.listener.PriceSourceRunnableTask;
import com.trader.trade.SecurityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * <pre>
 * User Story: As a trader I want to be able to monitor stock prices such
 * that when they breach a trigger level orders can be executed automatically
 * </pre>
 */
@AllArgsConstructor
@Getter
public class TradeExecutor {

    private final ExecutionService tradeExecutionService;
    private final PriceSourceRunnableTask priceSourceRunnableTask;

    public void autoExecuteBuyTrade(List<SecurityDto> request) throws InterruptedException {
        request.stream().map(
                req -> new BuyPriceListener(req.getSecurity(), req.getPriceThreshold(), req.getVolume(),
                        tradeExecutionService, false)).forEach(priceSourceRunnableTask::addPriceListener);
        Thread thread = new Thread(priceSourceRunnableTask);
        thread.start();
        thread.join();
        request.stream().map(
                req -> new BuyPriceListener(req.getSecurity(), req.getPriceThreshold(), req.getVolume(),
                        tradeExecutionService, false)).forEach(priceSourceRunnableTask::removePriceListener);
    }

    // This is like a test harness
    public static void main(String[] args) throws InterruptedException {
        TradeExecutor tradeExecutor = new TradeExecutor(new ExecutionServiceImpl(1),
                new PriceSourceImpl());
        final SecurityDto ibm = SecurityDto.builder().security("IBM").priceThreshold(55.00).volume(100)
                .build();
        final SecurityDto google = SecurityDto.builder().security("GOOGLE").priceThreshold(100.00)
                .volume(24)
                .build();
        tradeExecutor.autoExecuteBuyTrade(asList(ibm, google));
    }


}


