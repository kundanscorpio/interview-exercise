package com.trader.execution.service.strategy;

import com.trader.listener.PriceListener;
import com.trader.listener.PriceSourceImpl;
import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PriceSourceImplTest {


    @Test
    public void shouldAddAPriceListenerToPriceSource() {
        PriceListener priceListener = Mockito.mock(PriceListener.class);
        PriceSourceImpl priceSource = new PriceSourceImpl();
        priceSource.addPriceListener(priceListener);
        assertEquals(priceSource.getPriceListeners().size(), 1);
    }

    @Test
    public void shouldAddTwoPriceListenersToPriceSource() {
        PriceListener priceListener1 = Mockito.mock(PriceListener.class);
        PriceListener priceListener2 = Mockito.mock(PriceListener.class);
        PriceSourceImpl priceSource = new PriceSourceImpl();
        priceSource.addPriceListener(priceListener1);
        priceSource.addPriceListener(priceListener2);
        assertEquals(priceSource.getPriceListeners().size(), 2);
    }

    @Test
    public void shouldRemovePriceListenerFromPriceSource() {
        PriceListener priceListener1 = Mockito.mock(PriceListener.class);
        PriceListener priceListener2 = Mockito.mock(PriceListener.class);
        PriceSourceImpl priceSource = new PriceSourceImpl();
        priceSource.addPriceListener(priceListener1);
        priceSource.addPriceListener(priceListener2);
        priceSource.removePriceListener(priceListener2);
        assertEquals(priceSource.getPriceListeners().size(), 1);
    }

    @Test
    @SneakyThrows
    public void givenOneListenerPriceSourceShouldInvokeTheListenerWhenThreadStarted() {
        PriceListener priceListener = Mockito.mock(PriceListener.class);
        PriceSourceImpl priceSource1 = new PriceSourceImpl();
        priceSource1.addPriceListener(priceListener);
        Thread thread = new Thread(priceSource1);
        thread.start();
        thread.join();
        verify(priceListener, times(20)).priceUpdate(anyString(), anyDouble());
    }

    @Test
    @SneakyThrows
    public void testWithOneListenerPriceSourceShouldInvokeTheListenerWithSecurityAndPriceWhenThreadStarted() {
        List<String> SECURITIES = Arrays
                .asList("MICROSOFT", "IBM", "GOOGLE", "TCS", "WIPRO", "APPLE");
        ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        PriceListener priceListener = Mockito.mock(PriceListener.class);
        PriceSourceImpl priceSource = new PriceSourceImpl();
        priceSource.addPriceListener(priceListener);
        Thread thread = new Thread(priceSource);
        thread.start();
        thread.join();
        verify(priceListener, times(20)).priceUpdate(securityCaptor.capture(), priceCaptor.capture());
        assertThat(securityCaptor.getValue()).as("Should contain at least one value from Securities ")
                .matches(s -> SECURITIES.stream().anyMatch(s::contains));
        assertThat(priceCaptor.getValue()).as("Price should be a double value between 1.00 to 200.00")
                .isGreaterThan(1.00).isLessThanOrEqualTo(200.00);
    }

}
