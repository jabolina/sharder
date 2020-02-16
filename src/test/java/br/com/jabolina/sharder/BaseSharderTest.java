package br.com.jabolina.sharder;

import br.com.jabolina.sharder.cluster.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author jab
 * @date 1/11/20
 */
public abstract class BaseSharderTest {
  protected static final int NRO_NODES = 5;
  private static final int NRO_REPLICATION = 3;
  protected static int basePort = 5000;
  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseSharderTest.class);
  protected List<Sharder> instances = new ArrayList<>();

  public Node buildNode(int idx) {
    return Node.builder()
        .withNodeName("node-" + idx)
        .build();
  }

  protected Node[] buildNodes(int size) {
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

  Sharder buildSharder(String name) {
    return Sharder.builder()
        .withClusterName(name)
        .withNodes(buildNodes(NRO_NODES))
        .withAddress("127.0.0.1")
        .withPort(port())
        .withReplication(NRO_REPLICATION)
        .build();
  }

  CompletableFuture<Sharder> startInstance(Sharder sharder) {
    return sharder.start().thenApply(r -> sharder);
  }

  protected void removeFolders(String prefix) throws IOException {
    Path directory = Paths.get(prefix);

    if (Files.exists(directory)) {
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

  protected CompletableFuture<Void> wrap(Runnable runnable) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    return CompletableFuture.runAsync(new ThreadWrapper(runnable, future));
  }

  private class ThreadWrapper implements Runnable {
    private final Runnable runnable;
    private final CompletableFuture<?> future;

    ThreadWrapper(Runnable runnable, CompletableFuture future) {
      this.runnable = runnable;
      this.future = future;
    }

    @Override
    public void run() {
      runnable.run();
      future.complete(null);
    }
  }
}
