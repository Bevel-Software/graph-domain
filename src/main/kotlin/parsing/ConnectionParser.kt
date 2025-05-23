package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.Connection
import software.bevel.graph_domain.graph.Graphlike

/**
 * Defines a contract for parsers that can identify and create [Connection]s within a [Graphlike] structure.
 * Implementations of this interface are responsible for analyzing specified nodes or files to determine
 * how they connect to other elements in the graph.
 */
interface ConnectionParser {

    /**
     * Creates inbound connections for the specified nodes within the given graph.
     * This method analyzes the provided `nodes` to find other nodes in the `graph` that connect *to* them.
     *
     * @param nodes A vararg array of node IDs for which to find and create inbound connections.
     * @param graph The [Graphlike] context in which to find connections.
     * @return A list of [Connection] objects representing the identified inbound connections.
     */
    fun createInboundConnectionsForNodes(vararg nodes: String, graph: Graphlike): List<Connection>

    /**
     * Creates outbound connections for the specified nodes within the given graph.
     * This method analyzes the provided `nodes` to find other nodes in the `graph` that they connect *from*.
     *
     * @param nodes A vararg array of node IDs for which to find and create outbound connections.
     * @param graph The [Graphlike] context in which to find connections.
     * @return A list of [Connection] objects representing the identified outbound connections.
     */
    fun createOutboundConnectionsForNodes(vararg nodes: String, graph: Graphlike): List<Connection>

    /**
     * Creates outbound connections for all nodes found within the specified files, in the context of the given graph.
     * This method typically involves parsing the `files` to identify nodes and then determining their outbound connections
     * within the `graph`.
     *
     * @param files A vararg array of file paths or identifiers to parse for nodes and their connections.
     * @param graph The [Graphlike] context in which to find connections.
     * @return A list of [Connection] objects representing the identified outbound connections from nodes in the files.
     */
    fun createOutboundConnectionsForAllNodesInFiles(vararg files: String, graph: Graphlike): List<Connection>
}