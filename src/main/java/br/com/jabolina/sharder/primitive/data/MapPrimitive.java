package br.com.jabolina.sharder.primitive.data;

import com.google.common.base.MoreObjects;
import io.atomix.utils.serializer.Serializer;

/**
 * When using map primitives, the information will be hold by this class.
 * To be transported across nodes
 *
 * @author jabolina
 * @date 2/9/20
 */
public class MapPrimitive<K, V> extends AbstractPrimitive {
  private final K key;
  private final V value;

  public MapPrimitive(String primitiveName, K key, V value) {
    super(primitiveName);
    this.key = key;
    this.value = value;
  }

  public K key() {
    return key;
  }

  public V value() {
    return value;
  }

  @Override
  public byte[] serialize() {
    return serializer().encode(this);
  }

  @Override
  public Serializer serializer() {
    return Serializer.builder()
        .addType(String.class)
        .addType(MapPrimitive.class)
        .addType(key.getClass())
        .addType(value.getClass())
        .build();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", primitiveName())
        .add("key", key)
        .add("value", value)
        .toString();
  }
}
