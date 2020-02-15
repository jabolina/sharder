package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.utils.contract.Converter;

import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/15/20
 */
public abstract class AtomixRequestHandler<T, U> implements Function<T, U> {
  protected final AtomixWrapper wrapper;

  protected AtomixRequestHandler(AtomixWrapper wrapper) {
    this.wrapper = wrapper;
  }

  protected abstract Converter<T, U> converter();

  @Override
  public U apply(T t) {
    return converter().convert(t);
  }
}
