package br.com.jabolina.sharder;

import br.com.jabolina.sharder.primitive.Action;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
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
  public void testMapUsingSharder() throws InterruptedException, ExecutionException, TimeoutException {
    Sharder sharder = startInstance(buildSharder("sharder-test-map")).get(30, TimeUnit.SECONDS);
    Assert.assertNotNull(sharder);
    CountDownLatch latch = new CountDownLatch(1);

    TestPrimitive testPrimitive = new TestPrimitive();
    testPrimitive.value = TEST_VALUE;
    sharder.primitive("test-map", "test-key", testPrimitive, Action.WRITE)
        .whenComplete((res, err) -> latch.countDown());
    latch.await(10, TimeUnit.SECONDS);
    // Thread.sleep(10_000);
  }

  @Test
  public void testCollectionUsingSharder() throws InterruptedException, ExecutionException, TimeoutException {
    Sharder sharder = startInstance(buildSharder("sharder-test-collection")).get(30, TimeUnit.SECONDS);
    Assert.assertNotNull(sharder);
    CountDownLatch latch = new CountDownLatch(1);

    TestPrimitive testPrimitive = new TestPrimitive();
    testPrimitive.value = TEST_VALUE;
    sharder.primitive("test-collection", testPrimitive)
        .whenComplete((res, err) -> latch.countDown());

    latch.await(10, TimeUnit.SECONDS);
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
