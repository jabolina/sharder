package br.com.jabolina.sharder.message.atomix.operation;

import br.com.jabolina.sharder.message.Operation;
import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 *
 * @author jabolina
 * @date 2/2/20
 */
public abstract class AbstractAtomixOperation<T extends AbstractAtomixOperation<T>> implements Operation {
  private final AbstractPrimitive primitive;
  private final OperationType type;

  protected AbstractAtomixOperation(AbstractPrimitive primitive, OperationType type) {
    this.type = type;
    this.primitive = primitive;
  }

  /**
   * Build the operation concrete implementation
   *
   * @param primitive: which primitive is being executed
   * @return built operation
   */
  public abstract T build(AbstractPrimitive primitive);

  /**
   * Returns to which bucket this operation should be executed
   *
   * @param buckets: number of available nodes
   * @return which node should execute
   */
  @SuppressWarnings("UnstableApiUsage")
  public int hash(int buckets) {
    return Hashing.consistentHash(HashCode.fromBytes(primitive.primitiveName().getBytes(StandardCharsets.UTF_8)), buckets);
  }

  /**
   * Get operation type
   *
   * @return operation type
   */
  public OperationType type() {
    return type;
  }

  /**
   * Get operation primitive
   * @return operation primitive
   */
  public AbstractPrimitive primitive() {
    return primitive;
  }

  /**
   * Atomix operation type
   */
  public enum OperationType {
    /**
     * Execute operation.
     */
    EXECUTE,

    /**
     * Query operation.
     */
    QUERY,
  }

  @SuppressWarnings("unchecked")
  public abstract static class Builder<T extends AbstractAtomixOperation, B extends Builder<T, B>> implements Operation.Builder<T, B> {
    protected OperationType type;
    protected AbstractPrimitive primitive;

    public B withType(OperationType type) {
      this.type = type;
      return (B) this;
    }

    public B withPrimitive(AbstractPrimitive primitive) {
      this.primitive = Objects.requireNonNull(primitive, "Primitive cannot be null!");
      return (B) this;
    }
  }
}
