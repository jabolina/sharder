package br.com.jabolina.sharder.primitive.data;

import io.atomix.utils.serializer.Serializer;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class CollectionPrimitive<E> extends AbstractPrimitive {
  private final E element;

  public CollectionPrimitive(String primitiveName, E element) {
    super(primitiveName);
    this.element = element;
  }

  public E element() {
    return element;
  }

  @Override
  public byte[] serialize() {
    return serializer().encode(this);
  }

  @Override
  public Serializer serializer() {
    return Serializer.builder()
        .addType(String.class)
        .addType(CollectionPrimitive.class)
        .addType(element.getClass())
        .build();
  }
}
