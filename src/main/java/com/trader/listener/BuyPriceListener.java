package com.trader.listener;

import com.trader.execution.service.ExecutionService;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BuyPriceListener implements PriceListener {

    private final String security;
    private final double triggerLevel;
    private final int qtyToPurchase;
    private final ExecutionService tradeExecutionService;
    private boolean isTradeExecuted;

    /**
     * This keeps listening to price updates and execute BUY trade if conditions are met
     * @param security
     * @param price
     */
    @Override
    public void priceUpdate(String security, double price) {
        if (canExecuteBuyTrade(security, price)) {
            tradeExecutionService.buy(security, price, qtyToPurchase);
            isTradeExecuted = true;
        }
    }

    /**
     * This  returns true  if the current price is less than threshold price indicating that trade can be
     * executed
     * @param security
     * @param price
     * @return
     */
    private boolean canExecuteBuyTrade(String security, double price) {
        return (!isTradeExecuted) && this.security.equals(security) && (price < this.triggerLevel);
    }
}
