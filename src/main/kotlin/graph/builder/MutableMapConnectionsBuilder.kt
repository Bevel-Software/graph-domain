package software.bevel.graph_domain.graph.builder

import software.bevel.graph_domain.graph.Connection
import software.bevel.graph_domain.graph.ConnectionsNavigator
import software.bevel.graph_domain.graph.ConnectionType
import software.bevel.graph_domain.graph.ConnectionsBuilder

/**
 * A mutable, map-based implementation of [ConnectionsBuilder].
 * It uses nested maps to store connections for efficient lookup from both source-to-target and target-to-source perspectives.
 * This class also extends [MapConnectionsNavigator] to provide navigation capabilities over its current state.
 *
 * @property connectionsFromTo A mutable map where the outer key is the source node ID, the inner key is the target node ID,
 *                             and the value is a mutable set of [Connection]s from source to target.
 * @property connectionsToFrom A mutable map where the outer key is the target node ID, the inner key is the source node ID,
 *                             and the value is a mutable set of [Connection]s from source to target (stored with target as primary key for reverse lookup).
 */
class MutableMapConnectionsBuilder(
    override val connectionsFromTo: MutableMap<String, MutableMap<String, MutableSet<Connection>>> = mutableMapOf(),
    override val connectionsToFrom: MutableMap<String, MutableMap<String, MutableSet<Connection>>> = mutableMapOf()
): ConnectionsBuilder, MapConnectionsNavigator(connectionsFromTo, connectionsToFrom){

    /**
     * Secondary constructor that initializes the builder by adding all connections from an existing [ConnectionsNavigator].
     *
     * @param connectionsNavigator The [ConnectionsNavigator] to populate connections from.
     */
    constructor(connectionsNavigator: ConnectionsNavigator): this() {
        addAll(connectionsNavigator)
    }

    /**
     * Builds an immutable [MapConnectionsNavigator] from the current state of this builder.
     * The underlying maps are converted to immutable versions.
     *
     * @return A new [MapConnectionsNavigator] instance.
     */
    override fun build(): ConnectionsNavigator {
        return MapConnectionsNavigator(
            connectionsFromTo = connectionsFromTo.mapValues { it.value.mapValues { it.value.toSet() } },
            connectionsToFrom = connectionsToFrom.mapValues { it.value.mapValues { it.value.toSet() } }
        )
    }

    /**
     * Deletes all connections related to the specified node ID.
     * This involves removing the node ID as a key from the primary maps and also iterating through other nodes' connections
     * to remove any entries where the specified node ID is a secondary key (target or source respectively).
     *
     * @param nodeId The ID of the node whose connections are to be deleted.
     */
    override fun deleteConnectionsRelatedToNode(nodeId: String) {
        connectionsFromTo.remove(nodeId)
        connectionsFromTo.forEach { it.value.remove(nodeId) }
        connectionsToFrom.remove(nodeId)
        connectionsToFrom.forEach { it.value.remove(nodeId) }
    }

    /**
     * Deletes a specific connection from both internal maps.
     *
     * @param connection The [Connection] to delete.
     */
    override fun delete(connection: Connection) {
        connectionsFromTo[connection.sourceNodeName]?.get(connection.targetNodeName)?.remove(connection)
        connectionsToFrom[connection.targetNodeName]?.get(connection.sourceNodeName)?.remove(connection)
    }

    /**
     * Clears all connections from this builder by clearing the underlying maps.
     */
    override fun clear() {
        connectionsFromTo.clear()
        connectionsToFrom.clear()
    }

    /**
     * Merges connections from one node (`fromId`) into another node (`intoId`).
     * All connections where `fromId` was a source are re-added with `intoId` as the source.
     * All connections where `fromId` was a target are re-added with `intoId` as the target.
     * The original connections involving `fromId` are not explicitly removed by this method alone;
     * typically, this would be followed by [deleteConnectionsRelatedToNode] for `fromId`.
     *
     * @param intoId The ID of the node to merge connections into.
     * @param fromId The ID of the node whose connections are to be merged and effectively replaced by `intoId`.
     */
    override fun mergeNodes(intoId: String, fromId: String) {
        this.connectionsFromTo[fromId]?.forEach { connections ->
            connections.value.forEach { connection ->
                this.addConnection(
                    this.connectionsFromTo,
                    connection.copy(sourceNodeName = intoId),
                    intoId,
                    connection.targetNodeName
                )
                this.addConnection(
                    this.connectionsToFrom,
                    connection.copy(sourceNodeName = intoId),
                    connection.targetNodeName,
                    intoId
                )
            }
        }
        this.connectionsToFrom[fromId]?.forEach { connections ->
            connections.value.forEach { connection ->
                this.addConnection(
                    this.connectionsToFrom,
                    connection.copy(targetNodeName = intoId),
                    intoId,
                    connection.sourceNodeName
                )
                this.addConnection(
                    this.connectionsFromTo,
                    connection.copy(targetNodeName = intoId),
                    connection.sourceNodeName,
                    intoId
                )
            }
        }
    }

    /**
     * Adds all connections from another [ConnectionsNavigator] to this builder.
     * If `other` is a [MapConnectionsNavigator], it efficiently iterates through its internal maps.
     * Otherwise, it retrieves all connections from `other` and adds them one by one.
     *
     * @param other The [ConnectionsNavigator] to add connections from.
     */
    override fun addAll(other: ConnectionsNavigator) {
        if (other is MapConnectionsNavigator) {
            other.connectionsToFrom.values
                .flatMap { it.values }
                .flatten()
                .forEach { connection ->
                    addConnection(connectionsToFrom, connection, connection.targetNodeName, connection.sourceNodeName)
                }

            other.connectionsFromTo.values
                .flatMap { it.values }
                .flatten()
                .forEach { connection ->
                    addConnection(connectionsFromTo, connection, connection.sourceNodeName, connection.targetNodeName)
                }
            return
        }

        other.getAllConnections().forEach { connection ->
            this.addConnection(connection)
        }
    }

    /**
     * Adds a single connection to both internal maps, ensuring bidirectional indexing.
     *
     * @param connection The [Connection] to add.
     */
    override fun addConnection(connection: Connection) {
        addConnectionFromTo(
            connection,
            connection.sourceNodeName,
            connection.targetNodeName
        )
        addConnectionToFrom(
            connection,
            connection.targetNodeName,
            connection.sourceNodeName
        )
    }

    /**
     * Helper method to add a connection to the `connectionsFromTo` map.
     *
     * @param connection The [Connection] to add.
     * @param key1 The source node ID.
     * @param key2 The target node ID.
     */
    fun addConnectionFromTo(connection: Connection, key1: String, key2: String) {
        addConnection(connectionsFromTo, connection, key1, key2)
    }

    /**
     * Helper method to add a connection to the `connectionsToFrom` map.
     *
     * @param connection The [Connection] to add.
     * @param key1 The target node ID.
     * @param key2 The source node ID.
     */
    fun addConnectionToFrom(connection: Connection, key1: String, key2: String) {
        addConnection(connectionsToFrom, connection, key1, key2)
    }

    /**
     * Core helper method to add a connection to a specified nested map structure.
     * Initializes inner maps and sets if they don't exist.
     *
     * @param connectionMap The specific mutable map (e.g., `connectionsFromTo` or `connectionsToFrom`) to add to.
     * @param connection The [Connection] to add.
     * @param key1 The primary key for the `connectionMap` (e.g., source node ID).
     * @param key2 The secondary key for the inner map (e.g., target node ID).
     */
    fun addConnection(
        connectionMap: MutableMap<String, MutableMap<String, MutableSet<Connection>>>,
        connection: Connection,
        key1: String,
        key2: String
    ) {
        if (!connectionMap.containsKey(key1)) {
            connectionMap[key1] = mutableMapOf(key2 to mutableSetOf(connection))
        } else if (!connectionMap[key1]!!.containsKey(key2)) {
            connectionMap[key1]!![key2] = mutableSetOf(connection)
        } else {
            connectionMap[key1]!![key2]!!.add(connection)
        }
    }
}

/**
 * An open, map-based implementation of [ConnectionsNavigator] providing read-only access to connections.
 * This class serves as a base for [MutableMapConnectionsBuilder] and can be used for an immutable snapshot of connections.
 * The connections are stored in maps for efficient lookups.
 *
 * @property connectionsFromTo A map where the outer key is the source node ID, the inner key is the target node ID,
 *                             and the value is a set of [Connection]s from source to target.
 * @property connectionsToFrom A map where the outer key is the target node ID, the inner key is the source node ID,
 *                             and the value is a set of [Connection]s (indexed by target for reverse lookup).
 */
open class MapConnectionsNavigator(
    open val connectionsFromTo: Map<String, Map<String, Set<Connection>>> = mapOf(),
    open val connectionsToFrom: Map<String, Map<String, Set<Connection>>> = mapOf()
): ConnectionsNavigator {

    /**
     * Finds connections of a specific type between a source and target node.
     * @param sourceNodeName The source node's ID.
     * @param targetNodeName The target node's ID.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnections(sourceNodeName: String, targetNodeName: String, connectionType: ConnectionType): List<Connection> {
        return connectionsFromTo[sourceNodeName]?.get(targetNodeName)?.filter { it.connectionType == connectionType } ?: listOf()
    }

    /**
     * Finds all connections between a source and target node, regardless of type.
     * @param sourceNodeName The source node's ID.
     * @param targetNodeName The target node's ID.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnections(sourceNodeName: String, targetNodeName: String): List<Connection> {
        return connectionsFromTo[sourceNodeName]?.get(targetNodeName)?.toList() ?: listOf()
    }

    /**
     * Retrieves all connections where the given node ID is either the source or the target.
     * @param nodeId The ID of the node.
     * @return A list of all [Connection]s involving the specified node.
     */
    override fun getAllConnectionsContaining(nodeId: String): List<Connection> {
        return (connectionsFromTo[nodeId]?.values?.flatten() ?: listOf()) +
                (connectionsToFrom[nodeId]?.values?.flatten() ?: listOf())
    }

    /**
     * Retrieves all connections stored in this navigator.
     * @return A list of all [Connection]s.
     */
    override fun getAllConnections(): List<Connection> {
        return connectionsFromTo.values.flatMap { it.values }.flatten()
    }

    /**
     * Retrieves all connections of a specific type.
     * @param connectionType The type of connection to filter by.
     * @return A list of all [Connection]s of the specified type.
     */
    override fun getAllConnectionsOfType(connectionType: ConnectionType): List<Connection> {
        return getAllConnections().filter { it.connectionType == connectionType }
    }

    /**
     * Finds all connections of a specific type originating from a specific node.
     * @param nodeId The ID of the source node.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnectionsFromOfType(nodeId: String, connectionType: ConnectionType): List<Connection> {
        return connectionsFromTo[nodeId]?.values?.flatten()
            ?.filter { it.connectionType == connectionType }
            ?: listOf()
    }

    /**
     * Finds all connections of a specific type pointing to a specific node.
     * @param nodeId The ID of the target node.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnectionsToOfType(nodeId: String, connectionType: ConnectionType): List<Connection> {
        return connectionsToFrom[nodeId]?.values?.flatten()
            ?.filter { it.connectionType == connectionType }
            ?: listOf()
    }

    /**
     * Finds all connections pointing to a specific node (inbound connections).
     * @param nodeId The ID of the target node.
     * @return A list of [Connection]s where the specified node is the target.
     */
    override fun findConnectionsTo(nodeId: String): List<Connection> {
        return connectionsToFrom[nodeId]?.values?.flatten()
            ?: listOf()
    }

    /**
     * Finds all connections originating from a specific node (outbound connections).
     * @param nodeId The ID of the source node.
     * @return A list of [Connection]s where the specified node is the source.
     */
    override fun findConnectionsFrom(nodeId: String): List<Connection> {
        return connectionsFromTo[nodeId]?.values?.flatten()
            ?: listOf()
    }
}

/**
 * An open, list-based implementation of [ConnectionsNavigator] providing read-only access to connections.
 * This navigator operates on a pre-defined list of connections and filters them as requested.
 * It's suitable for scenarios where connections are fixed or derived from a source that provides a flat list.
 *
 * @property connections The list of [Connection]s this navigator operates on.
 */
open class ListConnectionsNavigator(
    /**
     * The list of [Connection]s this navigator operates on.
     */
    private val connections: List<Connection>
): ConnectionsNavigator {

    /**
     * Finds connections of a specific type between a source and target node.
     * @param sourceNodeName The source node's ID.
     * @param targetNodeName The target node's ID.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnections(sourceNodeName: String, targetNodeName: String, connectionType: ConnectionType): List<Connection> {
        return connections.filter { it.sourceNodeName == sourceNodeName && it.targetNodeName == targetNodeName && it.connectionType == connectionType}
    }

    /**
     * Finds all connections between a source and target node, regardless of type.
     * @param sourceNodeName The source node's ID.
     * @param targetNodeName The target node's ID.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnections(sourceNodeName: String, targetNodeName: String): List<Connection> {
        return connections.filter { it.sourceNodeName == sourceNodeName && it.targetNodeName == targetNodeName }
    }

    /**
     * Retrieves all connections where the given node ID is either the source or the target.
     * @param nodeId The ID of the node.
     * @return A list of all [Connection]s involving the specified node.
     */
    override fun getAllConnectionsContaining(nodeId: String): List<Connection> {
        return connections.filter { it.sourceNodeName == nodeId || it.targetNodeName == nodeId }
    }

    /**
     * Retrieves all connections stored in this navigator.
     * @return A list of all [Connection]s.
     */
    override fun getAllConnections(): List<Connection> {
        return connections
    }

    /**
     * Retrieves all connections of a specific type.
     * @param connectionType The type of connection to filter by.
     * @return A list of all [Connection]s of the specified type.
     */
    override fun getAllConnectionsOfType(connectionType: ConnectionType): List<Connection> {
        return connections.filter { it.connectionType == connectionType }
    }

    /**
     * Finds all connections of a specific type originating from a specific node.
     * @param nodeId The ID of the source node.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnectionsFromOfType(nodeId: String, connectionType: ConnectionType): List<Connection> {
        return connections.filter { it.sourceNodeName == nodeId && it.connectionType == connectionType }
    }

    /**
     * Finds all connections of a specific type pointing to a specific node.
     * @param nodeId The ID of the target node.
     * @param connectionType The type of connection to find.
     * @return A list of matching [Connection]s, or an empty list if none found.
     */
    override fun findConnectionsToOfType(nodeId: String, connectionType: ConnectionType): List<Connection> {
        return connections.filter { it.targetNodeName == nodeId && it.connectionType == connectionType }
    }

    /**
     * Finds all connections pointing to a specific node (inbound connections).
     * @param nodeId The ID of the target node.
     * @return A list of [Connection]s where the specified node is the target.
     */
    override fun findConnectionsTo(nodeId: String): List<Connection> {
        return connections.filter { it.targetNodeName == nodeId }
    }

    /**
     * Finds all connections originating from a specific node (outbound connections).
     * @param nodeId The ID of the source node.
     * @return A list of [Connection]s where the specified node is the source.
     */
    override fun findConnectionsFrom(nodeId: String): List<Connection> {
        return connections.filter { it.sourceNodeName == nodeId }
    }
}