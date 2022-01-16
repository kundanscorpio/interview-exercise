package com.trader.execution.service.strategy;
import com.trader.execution.service.ExecutionService;
import com.trader.execution.strategy.TradeExecutor;
import com.trader.listener.PriceListener;
import com.trader.listener.PriceSourceImpl;
import com.trader.trade.SecurityDto;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TradingStrategyTest {

    public static final double PRICE_THRESHOLD = 55.00;

    @SneakyThrows
    @Test
    public void testAutoExecutedBuyTradeSuccessWhenConditionsAreMet() {
        ExecutionService tradeExecutionService = Mockito.mock(ExecutionService.class);
        PriceSourceImpl priceSource = new MockPriceSource("IBM", 25.00);
        ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> volumeCaptor = ArgumentCaptor.forClass(Integer.class);
        TradeExecutor tradeExecutor = new TradeExecutor(tradeExecutionService, priceSource);
        List<SecurityDto> input = Arrays.asList(new SecurityDto("IBM", PRICE_THRESHOLD, 10));
        tradeExecutor.autoExecuteBuyTrade(input);
        verify(tradeExecutionService, times(1))
                .buy(securityCaptor.capture(), priceCaptor.capture(), volumeCaptor.capture());
        assertThat(securityCaptor.getValue()).isEqualTo("IBM");
        assertThat(priceCaptor.getValue()).isEqualTo(25.00);
        assertThat(volumeCaptor.getValue()).isEqualTo(10);
    }

    @SneakyThrows
    @Test
    public void testAutoBuyForNotSuccessfulBuy() {
        ExecutionService tradeExecutionService = Mockito.mock(ExecutionService.class);
        PriceSourceImpl priceSource = new MockPriceSource("IBM", 65.00);

        TradeExecutor tradeExecutor = new TradeExecutor(tradeExecutionService, priceSource);
        List<SecurityDto> input = Arrays.asList(new SecurityDto("IBM", PRICE_THRESHOLD, 100));
        tradeExecutor.autoExecuteBuyTrade(input);
        verifyZeroInteractions(tradeExecutionService);
    }

    @AllArgsConstructor
    private class MockPriceSource extends PriceSourceImpl {

        String security;
        double price;

        private final List<PriceListener> priceListeners = new CopyOnWriteArrayList<>();

        @Override
        public void addPriceListener(PriceListener listener) {
            priceListeners.add(listener);
        }

        @Override
        public void removePriceListener(PriceListener listener) {
            priceListeners.remove(listener);
        }

        @Override
        public void run() {
            priceListeners.forEach(priceListener -> priceListener.priceUpdate(security, price));
        }
    }
}
