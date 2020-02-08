package br.com.jabolina.sharder;

import br.com.jabolina.sharder.cluster.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author jab
 * @date 1/11/20
 */
public abstract class BaseSharderTest {
  private static final int NRO_NODES = 5;
  private static final int NRO_REPLICATION = 3;
  protected static int basePort = 5000;
  protected List<Sharder> instances = new ArrayList<>();

  public Node buildNode(int idx) {
    return Node.builder()
        .withNodeName("node-" + idx)
        .build();
  }

  public Node[] buildNodes(int size) {
    List<Node> nodes = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      nodes.add(buildNode(i));
    }

    return nodes.toArray(new Node[size]);
  }

  private int port() {
    if (instances.isEmpty()) {
      return basePort++;
    }

    return basePort++ + (NRO_REPLICATION * NRO_NODES);
  }

  protected Sharder buildSharder(String name) {
    return Sharder.builder()
        .withClusterName(name)
        .withNodes(buildNodes(NRO_NODES))
        .withAddress("127.0.0.1")
        .withPort(port())
        .withReplication(NRO_REPLICATION)
        .build();
  }

  public CompletableFuture<Sharder> startInstance(Sharder sharder) {
    return sharder.start().thenApply(r -> sharder);
  }
}
