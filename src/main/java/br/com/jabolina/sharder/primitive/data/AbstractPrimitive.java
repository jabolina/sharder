package br.com.jabolina.sharder.primitive.data;

import io.atomix.utils.serializer.Serializer;

import java.io.Serializable;

/**
 * Base primitive information holder to be transported
 *
 * @author jabolina
 * @date 2/9/20
 */
public abstract class AbstractPrimitive implements Serializable {
  private final String primitiveName;

  public AbstractPrimitive(String primitiveName) {
    this.primitiveName = primitiveName;
  }

  public String primitiveName() {
    return primitiveName;
  }

  public abstract byte[] serialize();

  public abstract Serializer serializer();
}
