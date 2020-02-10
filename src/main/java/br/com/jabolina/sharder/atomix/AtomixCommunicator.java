package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.node.Node;
import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;
import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;

import java.util.concurrent.CompletableFuture;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class AtomixCommunicator extends AbstractAtomixCommunicator {
  private final AtomixInvoker invoker;

  protected AtomixCommunicator(Node node, AtomixInvoker invoker) {
    super(node);
    this.invoker = invoker;
  }

  @Override
  public CompletableFuture<AtomixExecuteResponse> execute(AtomixExecuteRequest request) {
    AbstractAtomixOperation operation = request.operation();
    AbstractPrimitive primitive = operation.primitive();
    log.info("Received primitive {}", primitive);
    return super.execute(request);
  }

  @Override
  public CompletableFuture<AtomixQueryResponse> query(AtomixQueryRequest request) {
    return super.query(request);
  }
}
