package br.com.jabolina.sharder;

import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.communication.Address;
import br.com.jabolina.sharder.communication.multicast.MulticastComponent;
import br.com.jabolina.sharder.communication.multicast.NettyMulticast;
import br.com.jabolina.sharder.primitive.SharderPrimitiveClient;
import br.com.jabolina.sharder.registry.NodeRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class PrimitiveClientTest extends BaseSharderTest {
  private static final String TEST_VALUE = "test-primitive-value";

  @After
  @Before
  public void clear() throws IOException {
    removeFolders("management");
    removeFolders("partition");
  }

  @Test
  public void testMapPrimitiveClient() throws InterruptedException, ExecutionException, TimeoutException {
    ClusterConfiguration configuration = clusterConfiguration();
    NodeRegistry nodeRegistry = registryComponent(configuration);
    SharderPrimitiveClient primitiveClient = new SharderPrimitiveClient(nodeRegistry);
    nodeRegistry.start().get(30, TimeUnit.SECONDS);
    primitiveClient.start().get(30, TimeUnit.SECONDS);

    wrap(() -> {
      TestPrimitive testPrimitive = new TestPrimitive();
      testPrimitive.value = TEST_VALUE;
      primitiveClient.primitive("test-map", "test-key", testPrimitive);
      try {
        Thread.sleep(2_500);
      } catch (InterruptedException e) { }
    }).get(10, TimeUnit.SECONDS);

    nodeRegistry.stop().thenRun(primitiveClient::stop).get(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCollectionPrimitiveClient() throws InterruptedException, ExecutionException, TimeoutException {
    ClusterConfiguration configuration = clusterConfiguration();
    NodeRegistry nodeRegistry = registryComponent(configuration);
    SharderPrimitiveClient primitiveClient = new SharderPrimitiveClient(nodeRegistry);
    nodeRegistry.start().get(30, TimeUnit.SECONDS);
    primitiveClient.start().get(30, TimeUnit.SECONDS);

    wrap(() -> {
      TestPrimitive testPrimitive = new TestPrimitive();
      testPrimitive.value = TEST_VALUE;
      primitiveClient.primitive("test-collection", testPrimitive);
      try {
        Thread.sleep(2_500);
      } catch (InterruptedException e) { }
    }).get(10, TimeUnit.SECONDS);

    nodeRegistry.stop().thenRun(primitiveClient::stop).get(30, TimeUnit.SECONDS);
  }

  @Test
  public void testMapUsingSharder() throws InterruptedException, ExecutionException, TimeoutException {
    Sharder sharder = startInstance(buildSharder("sharder-test-map")).get(30, TimeUnit.SECONDS);
    Assert.assertNotNull(sharder);

    wrap(() -> {
      TestPrimitive testPrimitive = new TestPrimitive();
      testPrimitive.value = TEST_VALUE;
      sharder.primitive("test-map", "test-key", testPrimitive);
      try {
        Thread.sleep(2_500);
      } catch (InterruptedException e) { }
    }).get(10, TimeUnit.SECONDS);
  }

  @Test
  public void testCollectionUsingSharder() throws InterruptedException, ExecutionException, TimeoutException {
    Sharder sharder = startInstance(buildSharder("sharder-test-collection")).get(30, TimeUnit.SECONDS);
    Assert.assertNotNull(sharder);

    wrap(() -> {
      TestPrimitive testPrimitive = new TestPrimitive();
      testPrimitive.value = TEST_VALUE;
      sharder.primitive("test-collection", testPrimitive);
      try {
        Thread.sleep(2_500);
      } catch (InterruptedException e) { }
    }).get(10, TimeUnit.SECONDS);
  }

  private ClusterConfiguration clusterConfiguration() {
    return buildSharder("primitive-client-test")
        .configuration();
  }

  private NodeRegistry registryComponent(ClusterConfiguration configuration) {
    return NodeRegistry.builder()
        .withClusterConfiguration(configuration)
        .withMulticastMessaging(multicastComponent(configuration))
        .build();
  }

  private MulticastComponent multicastComponent(ClusterConfiguration configuration) {
    return NettyMulticast.builder()
        .withLocalAddr(Address.from(configuration.getAddress(), configuration.getPort()))
        .withGroupAddr(Address.from(
            configuration.getMulticastConfiguration().getGroup().getHostAddress(),
            configuration.getMulticastConfiguration().getPort(),
            configuration.getMulticastConfiguration().getGroup()))
        .build();
  }

  public class TestPrimitive implements Serializable {
    protected String value;

    @Override
    public String toString() {
      return "TestPrimitive{"
          + "value='" + value + '\''
          + '}';
    }
  }
}
