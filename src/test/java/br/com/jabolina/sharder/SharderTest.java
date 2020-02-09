package br.com.jabolina.sharder;

import br.com.jabolina.sharder.communication.Address;
import br.com.jabolina.sharder.communication.multicast.MulticastConfiguration;
import br.com.jabolina.sharder.communication.multicast.NettyMulticast;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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

  @Test
  public void testSendMessage() throws InterruptedException, ExecutionException, TimeoutException {
    final String subject = "test-message-subject";
    final String testContent = "test-message-content";
    CountDownLatch latch = new CountDownLatch(1);
    Consumer<byte[]> consumer = (byte[] res) -> {
      String recv = new String(res, StandardCharsets.UTF_8);
      Assert.assertNotNull(recv);
      Assert.assertEquals(testContent, recv);
      latch.countDown();
    };
    MulticastConfiguration configuration = new MulticastConfiguration();
    NettyMulticast multicast = NettyMulticast.builder()
        .withLocalAddr(Address.from("127.0.0.1", basePort))
        .withGroupAddr(Address.from(
            configuration.getGroup().getHostAddress(),
            configuration.getPort(),
            configuration.getGroup()))
        .build();

    multicast.start().get(30, TimeUnit.SECONDS);

    multicast.subscribe(subject, consumer);
    multicast.multicast(subject, testContent.getBytes(StandardCharsets.UTF_8));
    latch.await(30, TimeUnit.SECONDS);

    multicast.unsubscribe(subject, consumer);
    multicast.stop().get(30, TimeUnit.SECONDS);
  }
}
