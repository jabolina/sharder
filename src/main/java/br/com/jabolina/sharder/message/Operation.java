package br.com.jabolina.sharder.message;

/**
 * Base type for all Sharder commands that will be executed
 *
 * @author jabolina
 * @date 2/1/20
 */
public interface Operation {

  interface Builder<T extends Operation, B extends Builder<T, B>> extends br.com.jabolina.sharder.utils.contract.Builder<T> {

  }
}
