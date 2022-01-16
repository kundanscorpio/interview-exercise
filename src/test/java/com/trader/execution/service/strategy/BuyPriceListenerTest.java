package com.trader.execution.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.trader.execution.service.ExecutionService;

import com.trader.listener.BuyPriceListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class BuyPriceListenerTest {
  public static final double TRIGGER_LEVEL = 55.00;
  private ExecutionService executionService;
  private ArgumentCaptor<String> acString;
  private ArgumentCaptor<Double> acDouble;
  private ArgumentCaptor<Integer> acInteger;

  @Before
  public void init() {
    executionService = Mockito.mock(ExecutionService.class);
    acString = ArgumentCaptor.forClass(String.class);
    acDouble = ArgumentCaptor.forClass(Double.class);
    acInteger = ArgumentCaptor.forClass(Integer.class);
  }

  @Test
  public void testInitializeStateForBuyPriceListener() {

    BuyPriceListener buyPriceListener = new BuyPriceListener("IBM", TRIGGER_LEVEL, 100, executionService,
        false);

    assertThat(buyPriceListener.getSecurity()).isEqualTo("IBM");
    assertThat(buyPriceListener.getTriggerLevel()).isEqualTo(TRIGGER_LEVEL);
    assertThat(buyPriceListener.getQtyToPurchase()).isEqualTo(100);
    assertFalse(buyPriceListener.isTradeExecuted());
  }

  @Test
  public void testShouldExecuteBuyTradeWhenThresholdIsMet() {

    BuyPriceListener buyPriceListener = new BuyPriceListener("IBM", TRIGGER_LEVEL, 100, executionService,
        false);
    buyPriceListener.priceUpdate("IBM", 26.00);

    verify(executionService, times(1))
        .buy(acString.capture(), acDouble.capture(), acInteger.capture());
    assertThat(acString.getValue()).as("Should be IBM ")
        .isEqualTo("IBM");
    assertThat(acDouble.getValue()).as("Should be a value less than 55.00, and it is 26.00")
        .isEqualTo(26.00);
    assertThat(acInteger.getValue()).as("Should be the volume purchased").isEqualTo(100);
    assertThat(buyPriceListener.isTradeExecuted())
        .as("Trade is successfully executed").isTrue();
  }

  @Test
  public void testShouldNotExecuteBuyTradeWhenThresholdIsNotMet() {

    BuyPriceListener buyPriceListener = new BuyPriceListener("IBM", TRIGGER_LEVEL, 100, executionService,
        false);
    buyPriceListener.priceUpdate("IBM", 155.00);

    verify(executionService, times(0))
        .buy(acString.capture(), acDouble.capture(), acInteger.capture());
    assertThat(buyPriceListener.isTradeExecuted())
        .as("Trade is not executed successfully").isFalse();
  }

  @Test
  public void testShouldNotBuyWhenSecurityIsDifferent() {

    BuyPriceListener buyPriceListener = new BuyPriceListener("APPLE", TRIGGER_LEVEL, 100, executionService,
        false);
    buyPriceListener.priceUpdate("IBM", TRIGGER_LEVEL);

    verify(executionService, times(0))
        .buy(acString.capture(), acDouble.capture(), acInteger.capture());
    assertThat(buyPriceListener.isTradeExecuted())
        .as("Should not successfully execute buy trade because security is different").isFalse();
  }

  @Test
  public void testShouldExecuteBuyOnlyOnceWhenSeveralPriceUpdatesForATradeIsReceived() {

    BuyPriceListener buyPriceListener = new BuyPriceListener("IBM", TRIGGER_LEVEL, 100, executionService,
        false);
    buyPriceListener.priceUpdate("IBM", 20.00);
    buyPriceListener.priceUpdate("IBM", 10.00);
    buyPriceListener.priceUpdate("IBM", 35.00);

    verify(executionService, times(1))
        .buy(acString.capture(), acDouble.capture(), acInteger.capture());
    assertThat(acString.getValue()).as("Should be IBM ")
        .isEqualTo("IBM");
    assertThat(acDouble.getValue()).as("Should be the value less than 55.00, that is 20.00")
        .isEqualTo(20.00);
    assertThat(acInteger.getValue()).as("Should the volume of trade purchased is Equal to").isEqualTo(100);
    assertThat(buyPriceListener.isTradeExecuted())
        .as("Should the trade be successfully executed").isTrue();
  }
}
