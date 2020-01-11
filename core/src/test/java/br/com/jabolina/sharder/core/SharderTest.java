package br.com.jabolina.sharder.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jab
 * @date 1/11/20
 */
public class SharderTest extends BaseSharderTest {
  private static final int NRO_INSTANCES = 5;
  private static final Logger LOGGER = LoggerFactory.getLogger(SharderTest.class);
  private List<Sharder> instances = new ArrayList<>();

  @After
  public void teardown() throws InterruptedException, ExecutionException, TimeoutException {
    instances.forEach(Sharder::stop);
    LOGGER.info("Stopped all instances");
  }

  private void startAndInsertInstance(String name) throws InterruptedException, ExecutionException, TimeoutException {
    Sharder instance = startInstance(buildSharder(name)).get(30, TimeUnit.SECONDS);
    Assert.assertEquals(name, instance.getName());
    LOGGER.info("Created instance [{}]", instance.getName());
    instances.add(instance);
  }

  @Test
  public void testStartMultipleInstances() throws InterruptedException, ExecutionException, TimeoutException {
    for (int i = 0; i < NRO_INSTANCES; i++) {
      startAndInsertInstance("default-sharder-" + i);
    }

  }
}
