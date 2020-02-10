package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.communication.Communication;
import br.com.jabolina.sharder.message.atomix.request.AtomixExecuteRequest;
import br.com.jabolina.sharder.message.atomix.request.AtomixQueryRequest;
import br.com.jabolina.sharder.message.atomix.response.AtomixExecuteResponse;
import br.com.jabolina.sharder.message.atomix.response.AtomixQueryResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Communication between Atomix nodes and Sharder is executed through here.
 *
 * @author jabolina
 * @date 2/1/20
 */
public interface Communicator extends Communication {

  /**
   * Execute a command into Atomix cluster. The execute command consists of creation or
   * updating some value
   *
   * @param request: information about what will be executed
   * @return future with the execution on all Atomix clusters
   */
  CompletableFuture<AtomixExecuteResponse> execute(AtomixExecuteRequest request);

  /**
   * Executes a query into Atomix cluster. The query consists of reading some value.
   *
   * @param request: information about query to be executed
   * @return future with the query response on the right Atomix node
   */
  CompletableFuture<AtomixQueryResponse> query(AtomixQueryRequest request);

  interface Builder<T extends Communicator, U extends Builder<T, U>> extends br.com.jabolina.sharder.utils.contract.Builder<T> {
  }
}
