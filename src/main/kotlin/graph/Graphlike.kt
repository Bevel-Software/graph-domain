package software.bevel.graph_domain.graph

import software.bevel.graph_domain.graph.builder.MutableMapConnectionsBuilder

/**
 * Represents a graph-like structure containing nodes and connections.
 * This interface provides a read-only view of the graph's components.
 */
interface Graphlike {
    /** A map of node identifiers (String) to [Node] objects. */
    val nodes: Map<String, Node>
    /** A [ConnectionsNavigator] to query the connections within the graph. */
    val connections: ConnectionsNavigator

    /**
     * Allows accessing a node by its name using the array-access operator.
     *
     * @param name The unique identifier (name) of the node to retrieve.
     * @return The [Node] if found, or `null` otherwise.
     */
    operator fun get(name: String): Node? {
        return nodes[name]
    }
}

/**
 * A concrete implementation of [Graphlike].
 *
 * @property nodes A map of node identifiers (String) to [Node] objects.
 * @property connections A [ConnectionsNavigator] to query the connections within the graph.
 */
class Graph(
    override var nodes: Map<String, Node>,
    override var connections: ConnectionsNavigator,
): Graphlike {
    /**
     * Companion object for the [Graph] class.
     */
    companion object {
        /**
         * Converts a [Graphlike] instance to a [Graph] instance if it's not already one.
         * If the input is already a [Graph], it's returned directly.
         * Otherwise, a new [Graph] is created with a copy of the nodes and a new [MutableMapConnectionsBuilder]
         * initialized from the input graph's connections.
         *
         * @param graph The [Graphlike] instance to convert.
         * @return A [Graph] instance.
         */
        fun toGraphIfNotGraph(graph: Graphlike): Graph {
            return if(graph is Graph) graph else Graph(
                graph.nodes.toMap(),
                MutableMapConnectionsBuilder(graph.connections)
            )
        }
    }
}