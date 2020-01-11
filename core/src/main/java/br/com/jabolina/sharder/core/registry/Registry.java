package br.com.jabolina.sharder.core.registry;

import br.com.jabolina.sharder.core.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.core.cluster.Member;
import br.com.jabolina.sharder.core.utils.contract.Component;

import java.util.Objects;
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
public interface Registry<T extends Member> extends Component<Registry<T>> {

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

  abstract class Builder<U extends Member, R extends Registry<U>> implements br.com.jabolina.sharder.core.utils.contract.Builder<R> {
    protected RegistryConfiguration registryConfiguration;

    public Builder<U, R> withClusterConfiguration(ClusterConfiguration configuration) {
      this.registryConfiguration.setClusterConfiguration(
          Objects.requireNonNull(configuration, "Cluster configuration cannot be null for registry!"));
      return this;
    }
  }

}
