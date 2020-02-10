package br.com.jabolina.sharder.message.atomix.operation;

import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class ExecuteOperation extends AbstractAtomixOperation<ExecuteOperation> {
  private ExecuteOperation(AbstractPrimitive primitive) {
    super(primitive, OperationType.EXECUTE);
  }

  /**
   * Returns execute operation builder
   *
   * @return execute operation builder
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public ExecuteOperation build(AbstractPrimitive primitive) {
    return ExecuteOperation.builder()
        .withPrimitive(primitive)
        .withType(OperationType.EXECUTE)
        .build();
  }

  public static class Builder extends AbstractAtomixOperation.Builder<ExecuteOperation, Builder> {

    @Override
    public ExecuteOperation build() {
      return new ExecuteOperation(primitive);
    }
  }
}
