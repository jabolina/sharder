package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.communication.multicast.Multicast;
import br.com.jabolina.sharder.concurrent.ConcurrentContext;
import br.com.jabolina.sharder.message.AbstractSharderMessageResponse;
import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;
import br.com.jabolina.sharder.message.atomix.operation.ExecuteOperation;
import br.com.jabolina.sharder.message.atomix.operation.QueryOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import br.com.jabolina.sharder.primitive.Action;
import br.com.jabolina.sharder.primitive.PrimitiveHolder;
import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;
import br.com.jabolina.sharder.registry.NodeRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class DefaultAtomixClient implements AtomixClient {
  private final NodeRegistry nodeRegistry;
  private List<AtomixCommunicator> communicators;
  private final Multicast multicast;
  private final ConcurrentContext context;

  public DefaultAtomixClient(NodeRegistry nodeRegistry, ConcurrentContext context) {
    this.nodeRegistry = nodeRegistry;
    this.context = context;
    this.communicators = communicators();
    this.multicast = nodeRegistry.getRegistryConfiguration().getMulticastComponent();

  }

  @Override
  public CompletableFuture<AbstractSharderMessageResponse> primitive(PrimitiveHolder holder, AbstractPrimitive primitive, Action action) {
    // Writing something on the primitive
    if (Action.WRITE.equals(action)) {
      return execute(ExecuteOperation.builder()
          .withPrimitive(primitive)
          .build())
          .thenApply(res -> res);
    }

    // So is querying the node for a value
    return execute(QueryOperation.builder()
        .withPrimitive(primitive)
        .build())
        .thenApply(res -> res);
  }

  private CompletableFuture<AtomixExecuteResponse> execute(ExecuteOperation operation) {
    CompletableFuture<AtomixExecuteResponse> future = new CompletableFuture<>();
    final Consumer<byte[]> consumer = bytes -> hash(operation).execute(AtomixExecuteRequest.builder()
        .withOperation(operation)
        .build())
        .thenApply(future::complete);
    multicast.subscribe(operation.primitive().primitiveName(), consumer);
    return future;
  }

  private CompletableFuture<AtomixQueryResponse> execute(QueryOperation operation) {
    CompletableFuture<AtomixQueryResponse> future = new CompletableFuture<>();
    final Consumer<byte[]> consumer = bytes -> hash(operation).query(AtomixQueryRequest.builder()
        .withOperation(operation)
        .build())
        .thenApply(future::complete);
    multicast.subscribe(operation.primitive().primitiveName(), consumer);
    return future;
  }

  private List<AtomixCommunicator> communicators() {
    return nodeRegistry.members().stream()
        .map(node -> new AtomixCommunicator(node, new AtomixInvoker(node, this.context)))
        .collect(Collectors.toList());
  }

  private AtomixCommunicator hash(AbstractAtomixOperation operation) {
    // FIXME: insert new nodes on end
    communicators = communicators();
    int node = operation.hash(communicators.size());
    return communicators.get(node);
  }
}
