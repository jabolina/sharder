package br.com.jabolina.sharder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jab
 * @date 1/11/20
 */
public class SharderTest extends BaseSharderTest {
  private static final int NRO_INSTANCES = 1;
  private static final String ATOMIX_NODE_NAME = "%d-node-%s";
  private static final Logger LOGGER = LoggerFactory.getLogger(SharderTest.class);

  @After
  public void teardown() {
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

    Assert.assertEquals(NRO_INSTANCES, instances.size());
  }
}
