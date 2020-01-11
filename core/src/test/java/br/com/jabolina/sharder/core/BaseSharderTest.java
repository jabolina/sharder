package br.com.jabolina.sharder.core;

import br.com.jabolina.sharder.core.cluster.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author jab
 * @date 1/11/20
 */
public abstract class BaseSharderTest {
  private static final int BASE_PORT = 5000;
  private static final int NRO_NODES = 5;

  public Node buildNode(int idx) {
    return Node.builder()
        .withNodeName("node-" + idx)
        .withAddress("127.0.0.1")
        .withPort(BASE_PORT + idx)
        .build();
  }

  public Node[] buildNodes(int size) {
    List<Node> nodes = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      nodes.add(buildNode(i));
    }

    return nodes.toArray(new Node[size]);
  }

  public Sharder buildSharder(String name) {
    return Sharder.builder()
        .withClusterName(name)
        .withNodes(buildNodes(NRO_NODES))
        .build();
  }

  public CompletableFuture<Sharder> startInstance(Sharder sharder) {
    return sharder.start().thenApply(r -> sharder);
  }
}
