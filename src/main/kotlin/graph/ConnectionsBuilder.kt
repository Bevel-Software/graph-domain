package software.bevel.graph_domain.graph

/**
 * Defines an interface for building and managing a collection of [Connection]s.
 * This interface extends [ConnectionsNavigator] to also provide navigation capabilities.
 */
interface ConnectionsBuilder: ConnectionsNavigator {

    /**
     * Builds an immutable [ConnectionsNavigator] from the current state of the builder.
     *
     * @return A [ConnectionsNavigator] instance representing the built connections.
     */
    fun build(): ConnectionsNavigator

    /**
     * Deletes all connections where the given node ID is either the source or the target.
     *
     * @param nodeId The unique identifier of the node whose connections are to be deleted.
     */
    fun deleteConnectionsRelatedToNode(nodeId: String)

    /**
     * Deletes a specific connection.
     *
     * @param connection The [Connection] to delete.
     */
    fun delete(connection: Connection)

    /**
     * Deletes all connections in the provided iterable.
     * This method has a default implementation that iterates and calls [delete] for each connection.
     *
     * @param connections An iterable of [Connection] objects to delete.
     */
    fun deleteAll(connections: Iterable<Connection>) {
        connections.forEach { delete(it) }
    }

    /**
     * Removes all connections from the builder.
     */
    fun clear()

    /**
     * Merges connections related to one node (`fromId`) into another node (`intoId`).
     * This typically involves updating connections where `fromId` was a source or target
     * to now use `intoId` instead, and then removing `fromId` related connections.
     *
     * @param intoId The ID of the node to merge connections into.
     * @param fromId The ID of the node whose connections are to be merged from.
     */
    fun mergeNodes(intoId: String, fromId: String)

    /**
     * Adds a single connection to the builder.
     *
     * @param connection The [Connection] to add.
     */
    fun addConnection(connection: Connection)

    /**
     * Adds all connections from another [ConnectionsNavigator] to this builder.
     *
     * @param other The [ConnectionsNavigator] from which to add connections.
     */
    fun addAll(other: ConnectionsNavigator)
}