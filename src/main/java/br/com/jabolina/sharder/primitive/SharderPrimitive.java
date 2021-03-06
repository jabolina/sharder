package br.com.jabolina.sharder.primitive;

import br.com.jabolina.sharder.utils.contract.Component;

/**
 * @author jabolina
 * @date 2/8/20
 */
public interface SharderPrimitive extends SharderPrimitiveFactory, Component<SharderPrimitive> {

  interface Builder<T extends SharderPrimitive, U extends Builder<T, U>> extends SharderPrimitiveFactory.Builder<T, U> {

  }
}
