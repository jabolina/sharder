package br.com.jabolina.sharder.concurrent;

/**
 * @author jab
 * @date 1/11/20
 */
public interface ConcurrentFactory {

  ConcurrentContext context();

  default void close() { }
}
