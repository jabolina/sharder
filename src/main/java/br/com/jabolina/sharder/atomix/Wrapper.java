package br.com.jabolina.sharder.atomix;

import br.com.jabolina.sharder.cluster.ClusterConfiguration;
import br.com.jabolina.sharder.cluster.node.NodeConfiguration;
import br.com.jabolina.sharder.utils.contract.Component;
import com.google.common.collect.Sets;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.primitive.partition.ManagedPartitionGroup;
import io.atomix.protocols.raft.partition.RaftPartitionGroup;
import io.atomix.protocols.raft.partition.RaftPartitionGroupConfig;
import io.atomix.protocols.raft.partition.RaftStorageConfig;
import io.atomix.storage.StorageLevel;
import io.atomix.utils.net.Address;

import java.util.ArrayList;
import java.util.Collections;
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

    return atomix(clusterConfiguration, nodeConfiguration, AtomixBuilder::build);
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

  default ManagedPartitionGroup partitionGroup(NodeConfiguration nodeConfiguration, List<String> nodes) {
    return new RaftPartitionGroup(new RaftPartitionGroupConfig()
        .setName(nodeConfiguration.getNodeName() + "-partition")
        .setPartitionSize(nodes.size())
        .setPartitions(nodes.size())
        .setMembers(Sets.newConcurrentHashSet(nodes))
        .setStorageConfig(new RaftStorageConfig().setLevel(StorageLevel.MEMORY)
            .setDirectory(String.format("partition/%s", nodeConfiguration.getNodeName()))));
  }

  default ManagedPartitionGroup managementGroup(NodeConfiguration nodeConfiguration, List<String> nodes) {
    return new RaftPartitionGroup(new RaftPartitionGroupConfig()
        .setName(nodeConfiguration.getNodeName() + "-management")
        .setPartitionSize(nodes.size())
        .setPartitions(1)
        .setStorageConfig(new RaftStorageConfig()
            .setLevel(StorageLevel.MEMORY)
            .setDirectory(String.format("management/%s", nodeConfiguration.getNodeName())))
        .setMembers(Sets.newConcurrentHashSet(nodes)));
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
      String nodeId = String.format(NODE_NAME_TEMPLATE, nodeConfiguration.getNodeName(), i);
      nodes.add(Node.builder()
          .withId(nodeId)
          .withAddress(address)
          .build());
    }

    Address address = Address.from(
        clusterConfiguration.getMulticastConfiguration().getGroup().getHostAddress(),
        clusterConfiguration.getMulticastConfiguration().getPort() + id());

    return Atomix.builder()
        .withClusterId(clusterConfiguration.getClusterName())
        .withMemberId(clusterConfiguration.getClusterName())
        .withAddress(nodeConfiguration.atomixClusterAddress())
        .withProperties(properties)
        .withMulticastEnabled()
        .withMulticastAddress(address)
        .withPartitionGroups(partitionGroup(nodeConfiguration, Collections.singletonList(clusterConfiguration.getClusterName())))
        .withManagementGroup(managementGroup(nodeConfiguration, Collections.singletonList(clusterConfiguration.getClusterName())))
        .withMembershipProvider(new BootstrapDiscoveryProvider(nodes));
  }
}
