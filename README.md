# Sharder
An in-memory-data-grid to shard data across multiple replicas.

### What is this
This is a project for study purposes(much backed while reading across Atomix codebase), where will be implemented a library to 
help shard data across multiple replicas. Each replica will hold only a shard of the total information, if one of the replica 
starts to hold too much information, the data will be re-sharded.

For each replica, when failures occurs the shard of data cannot be lost, so each replica can replicate only itself and
replicate the data it holds. The data for this replicated-replica will be consistent as will be used Raft for consensus.

TL;DR distributed cache

### Roadmap

So I can remember what to do next.

* Cluster creation
    * Can create N replicas
    * The N replicas can communicate within the cluster
        * Object serialization
    * Auto discovery between clusters
        * SWIM
* Data sharding
    * Shard data between nodes within the cluster
    * Primitive data structures
        * Map
        * Queue
    * Data transfer between nodes within the cluster
    * Re-shard already existent data
