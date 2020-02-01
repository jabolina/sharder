package br.com.jabolina.sharder.communication;

import br.com.jabolina.sharder.message.SharderMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Base type for communication that will be wrapped. Communication between Sharder and Atomix
 * is an example of a wrapped communication.
 *
 * @author jabolina
 * @date 2/1/20
 */
public interface WrapperCommunication<T extends SharderMessage, U extends SharderMessage> extends Communication {

  /**
   * Issues a new request to be executed.
   *
   * @param request : request to be executed
   * @return a future to be completed with the request response
   */
  CompletableFuture<U> execute(T request);
}
