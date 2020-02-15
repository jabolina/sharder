package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.message.atomix.operation.AbstractAtomixOperation;

/**
 * Action executed on primitives can be of two kinds, either a WRITE or READ.
 * </p>
 * When issuing a command to a primitive, the type of action must me be given.
 *
 * @author jabolina
 * @date 2/15/20
 */
public enum Action {
  /**
   * Execute an write action on a primitive
   */
  WRITE {
    @Override
    AbstractAtomixOperation.OperationType equivalent() {
      return AbstractAtomixOperation.OperationType.EXECUTE;
    }
  },

  /**
   * Execute an read action on a primitive
   */
  READ {
    @Override
    AbstractAtomixOperation.OperationType equivalent() {
      return AbstractAtomixOperation.OperationType.QUERY;
    }
  };

  /**
   * Returns the equivalent operation type for each action
   *
   * @return equivalent operation type
   */
  abstract AbstractAtomixOperation.OperationType equivalent();
}
