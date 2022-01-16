package com.trader.listener;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
@Getter
public class PriceSourceImpl implements PriceSourceRunnableTask {

    private final List<PriceListener> priceListeners = new CopyOnWriteArrayList<>();

    private static final List<String> SECURITIES = Arrays.asList("MICROSOFT", "IBM", "GOOGLE", "TCS", "WIPRO", "APPLE");

    private static final double MIN_PRICE = 1.00;
    private static final double MAX_PRICE = 200.00;

    @Override
    public void addPriceListener(PriceListener listener) {
        this.priceListeners.add(listener);
    }

    @Override
    public void removePriceListener(PriceListener listener) {
        this.priceListeners.remove(listener);
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            String security = SECURITIES.get(random.nextInt(SECURITIES.size()));
            double price = MIN_PRICE + (MAX_PRICE - MIN_PRICE) * random.nextDouble();
            priceListeners.forEach(priceListener -> priceListener.priceUpdate(security, price));
        }
    }

}
