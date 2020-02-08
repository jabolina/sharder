package br.com.jabolina.sharder.registry;

import br.com.jabolina.sharder.utils.contract.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Registry for cluster. The registry instance will hold information about the node within the cluster.
 * </p>
 * The registry is used for communication between the nodes, to issue messages and data.
 *
 * @param <T>: registry for the given type
 *
 * @author jab
 * @date 1/11/20
 */
public interface Registry<T> extends Component<Registry<T>> {

  /**
   * Retrieve the registered members
   *
   * @return all registered members
   */
  Collection<T> members();

  /**
   * Register an element into registry
   *
   * @param t: element that will be registered
   * @return registered element
   */
  CompletableFuture<T> register(T t);

  /**
   * Member that wants to unregister from the registry
   *
   * @param t: member that will leave
   */
  CompletableFuture<Void> unregister(T t);

  interface Builder<T extends Registry, U extends Builder<T, U>> extends br.com.jabolina.sharder.utils.contract.Builder<T> {
  }

}
