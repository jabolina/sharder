package br.com.jabolina.sharder.cluster.atomix;

import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.cluster.node.NodeConfiguration;
import br.com.jabolina.sharder.utils.contract.Component;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.profile.Profile;
import io.atomix.utils.net.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 * @author jabolina
 * @date 2/1/20
 */
public interface Wrapper extends Component {
  String NODE_NAME_TEMPLATE = "%s-atomix-%d";

  /**
   * Return id to identify the node inside the cluster
   *
   * @return node id inside the cluster
   */
  int id();

  default Atomix atomix(
      ClusterConfiguration clusterConfiguration,
      NodeConfiguration nodeConfiguration) {
    Address address = Address.from(
        clusterConfiguration.getMulticastConfiguration().getGroup().getHostAddress(),
        clusterConfiguration.getMulticastConfiguration().getPort() + id());
    return atomix(clusterConfiguration, nodeConfiguration, builder ->
        builder
            .withProfiles(Profile.consensus(clusterConfiguration.getClusterName()))
            .withMulticastEnabled()
            .withMulticastAddress(address)
        .build());
  }

  default Atomix atomix(
      ClusterConfiguration clusterConfiguration,
      NodeConfiguration nodeConfiguration,
      Function<AtomixBuilder, Atomix> builder) {
    return atomix(clusterConfiguration, nodeConfiguration, new Properties(), builder);
  }

  default Atomix atomix(
      ClusterConfiguration clusterConfiguration,
      NodeConfiguration nodeConfiguration,
      Properties properties,
      Function<AtomixBuilder, Atomix> builder) {
    return builder.apply(atomix(clusterConfiguration, nodeConfiguration, properties));
  }

  default AtomixBuilder atomix(
      ClusterConfiguration clusterConfiguration,
      NodeConfiguration nodeConfiguration,
      Properties properties) {
    List<Node> nodes = new ArrayList<>();

    for (int i = 0; i < clusterConfiguration.replication(); i++) {
      Address address = Address.from(
          nodeConfiguration.atomixNodeAddress().host(),
          nodeConfiguration.atomixNodeAddress().port() + i);
      nodes.add(Node.builder()
          .withId(String.format(NODE_NAME_TEMPLATE, nodeConfiguration.getNodeName(), i))
          .withAddress(address)
          .build());
    }

    return Atomix.builder()
        .withClusterId(clusterConfiguration.getClusterName())
        .withMemberId(clusterConfiguration.getClusterName())
        .withAddress(nodeConfiguration.atomixClusterAddress())
        .withProperties(properties)
        .withMulticastEnabled()
        .withMembershipProvider(new BootstrapDiscoveryProvider(nodes));
  }
}
