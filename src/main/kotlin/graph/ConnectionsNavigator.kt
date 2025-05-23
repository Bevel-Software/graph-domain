package software.bevel.graph_domain.graph

/**
 * Defines an interface for navigating and querying a collection of [Connection]s.
 * This interface provides read-only access to connections.
 */
interface ConnectionsNavigator {

    /**
     * Finds all connections between a specific source node and a specific target node.
     *
     * @param sourceNodeName The unique identifier of the source node.
     * @param targetNodeName The unique identifier of the target node.
     * @return A list of [Connection]s matching the criteria, or an empty list if none are found.
     */
    fun findConnections(sourceNodeName: String, targetNodeName: String): List<Connection>

    /**
     * Finds all connections of a specific type between a specific source node and a specific target node.
     *
     * @param sourceNodeName The unique identifier of the source node.
     * @param targetNodeName The unique identifier of the target node.
     * @param connectionType The [ConnectionType] to filter by.
     * @return A list of [Connection]s matching the criteria, or an empty list if none are found.
     */
    fun findConnections(sourceNodeName: String, targetNodeName: String, connectionType: ConnectionType): List<Connection>

    /**
     * Retrieves all connections where the given node ID is either the source or the target.
     *
     * @param nodeId The unique identifier of the node.
     * @return A list of all [Connection]s involving the specified node, or an empty list if none are found.
     */
    fun getAllConnectionsContaining(nodeId: String): List<Connection>

    /**
     * Retrieves all connections present in the navigator.
     *
     * @return A list of all [Connection]s, or an empty list if there are no connections.
     */
    fun getAllConnections(): List<Connection>

    /**
     * Retrieves all connections of a specific type.
     *
     * @param connectionType The [ConnectionType] to filter by.
     * @return A list of all [Connection]s of the specified type, or an empty list if none are found.
     */
    fun getAllConnectionsOfType(connectionType: ConnectionType): List<Connection>

    /**
     * Finds all connections of a specific type originating from a specific node.
     *
     * @param nodeId The unique identifier of the source node.
     * @param connectionType The [ConnectionType] to filter by.
     * @return A list of [Connection]s matching the criteria, or an empty list if none are found.
     */
    fun findConnectionsFromOfType(nodeId: String, connectionType: ConnectionType): List<Connection>

    /**
     * Finds all connections of a specific type pointing to a specific node.
     *
     * @param nodeId The unique identifier of the target node.
     * @param connectionType The [ConnectionType] to filter by.
     * @return A list of [Connection]s matching the criteria, or an empty list if none are found.
     */
    fun findConnectionsToOfType(nodeId: String, connectionType: ConnectionType): List<Connection>

    /**
     * Finds all connections pointing to a specific node (inbound connections).
     *
     * @param nodeId The unique identifier of the target node.
     * @return A list of [Connection]s where the specified node is the target, or an empty list if none are found.
     */
    fun findConnectionsTo(nodeId: String): List<Connection>

    /**
     * Finds all connections originating from a specific node (outbound connections).
     *
     * @param nodeId The unique identifier of the source node.
     * @return A list of [Connection]s where the specified node is the source, or an empty list if none are found.
     */
    fun findConnectionsFrom(nodeId: String): List<Connection>
}