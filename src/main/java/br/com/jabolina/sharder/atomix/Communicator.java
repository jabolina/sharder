package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.communication.WrapperCommunication;
import br.com.jabolina.sharder.message.atomix.AtomixMessage;

/**
 * @author jabolina
 * @date 2/1/20
 */
public interface Communicator extends WrapperCommunication<AtomixMessage, AtomixMessage> {

  //TODO: create request/response, request must have converter to command, response must have status
}
