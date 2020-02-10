package br.com.jabolina.sharder.message.atomix.operation;

import br.com.jabolina.sharder.primitive.data.AbstractPrimitive;

/**
 * @author jabolina
 * @date 2/9/20
 */
public class QueryOperation extends AbstractAtomixOperation<QueryOperation> {
  private QueryOperation(AbstractPrimitive primitive) {
    super(primitive, OperationType.QUERY);
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
  public QueryOperation build(AbstractPrimitive primitive) {
    return QueryOperation.builder()
        .withPrimitive(primitive)
        .withType(OperationType.QUERY)
        .build();
  }

  public static class Builder extends AbstractAtomixOperation.Builder<QueryOperation, Builder> {

    @Override
    public QueryOperation build() {
      return new QueryOperation(primitive);
    }
  }
}
