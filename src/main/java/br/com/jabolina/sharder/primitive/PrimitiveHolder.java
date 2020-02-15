package br.com.jabolina.sharder.primitive;

/**
 * Holds information about primitive
 *
 * @author jabolina
 * @date 2/8/20
 */
public class PrimitiveHolder {
  private final String name;
  private final String type;

  PrimitiveHolder(String name, String type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Get primitive name
   *
   * @return primitive name
   */
  public String name() {
    return name;
  }

  /**
   * Get primitive type
   *
   * @return primitive type
   */
  public String type() {
    return type;
  }
}
