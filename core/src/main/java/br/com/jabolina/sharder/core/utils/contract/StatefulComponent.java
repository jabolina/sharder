package br.com.jabolina.sharder.core.utils.contract;

import br.com.jabolina.sharder.core.utils.enumeration.ComponentStatus;

/**
 * Create a stateful component
 *
 * @param <T>: the component
 *
 * @author jab
 * @date 1/11/20
 */
public interface StatefulComponent<T> extends Component<T> {

  /**
   * Update component status
   *
   * @param updated: most recent component status
   * @return component
   */
  T updateStatus(ComponentStatus updated);

  /**
   * Get current component status
   *
   * @return component status
   */
  ComponentStatus currentStatus();
}
