package br.com.jabolina.sharder.registry;

import br.com.jabolina.sharder.cluster.Member;

/**
 * @author jabolina
 * @date 2/8/20
 */
public interface MemberRegistry<T extends Member> extends Registry<T> {

  abstract class Builder<T extends MemberRegistry, U extends Builder<T, U>> implements Registry.Builder<T, U> {
    protected RegistryConfiguration registryConfiguration = new RegistryConfiguration();
  }
}
