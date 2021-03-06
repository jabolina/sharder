package br.com.jabolina.sharder.utils.contract;

/**
 * Convert object from type T to U
 *
 * @author jabolina
 * @date 2/15/20
 */
@FunctionalInterface
public interface Converter<T, U> {

  /**
   * Convert the element
   * @param t: element to be converted
   * @return converted element
   */
  U convert(T t);
}
