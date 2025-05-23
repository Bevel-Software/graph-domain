package software.bevel.graph_domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.bevel.graph_domain.graph.NodeType
import software.bevel.graph_domain.graph.builder.FullyQualifiedNodeBuilder
import software.bevel.graph_domain.graph.builder.GraphBuilder

/**
 * A utility class for calculating and logging metrics about a [GraphBuilder] instance.
 */
class Metrics {
    /**
     * Companion object containing utility methods for metrics.
     */
    companion object {
        /**
         * Calculates and logs various metrics from the given [GraphBuilder].
         *
         * Metrics logged include:
         * - Total number of nodes.
         * - Total number of connections.
         * - Number of function nodes.
         * - Number of class nodes.
         * - Number of package nodes.
         * - Number of connections between functions, classes, or packages.
         *
         * @param graphBuilder The [GraphBuilder] instance to extract metrics from.
         * @param logger The [Logger] instance to use for logging metrics. Defaults to a logger for the [Metrics] class.
         */
        fun getMetrics(graphBuilder: GraphBuilder, logger: Logger = LoggerFactory.getLogger(Metrics::class.java)) {
            try {
                logger.info("Nodes: ${graphBuilder.nodes.size}")
                logger.info("Connections: ${graphBuilder.connectionsBuilder.getAllConnections().size}")
                val functions = graphBuilder.nodes.values.filterIsInstance<FullyQualifiedNodeBuilder>().count { it.nodeType == NodeType.Function }
                logger.info("Functions: $functions")
                val classes = graphBuilder.nodes.values.filterIsInstance<FullyQualifiedNodeBuilder>().count { it.nodeType == NodeType.Class }
                logger.info("Classes: $classes")
                val packages = graphBuilder.nodes.values.filterIsInstance<FullyQualifiedNodeBuilder>().count { it.nodeType == NodeType.Package }
                logger.info("Packages: $packages")
                val connections = graphBuilder.connectionsBuilder.getAllConnections().count { connection ->
                    graphBuilder.nodes.containsKey(connection.sourceNodeName) && graphBuilder.nodes.containsKey(connection.targetNodeName)
                            && (getNode(graphBuilder, connection.sourceNodeName).nodeType == NodeType.Class || getNode(graphBuilder, connection.sourceNodeName).nodeType == NodeType.Function || getNode(graphBuilder, connection.sourceNodeName).nodeType == NodeType.Package)
                            && (getNode(graphBuilder, connection.targetNodeName).nodeType == NodeType.Class || getNode(graphBuilder, connection.targetNodeName).nodeType == NodeType.Function || getNode(graphBuilder, connection.targetNodeName).nodeType == NodeType.Package)
                }
                logger.info("Connections between functions/classes/packages: $connections")
            } catch (ex: Exception) {
                logger.error("Error getting metrics", ex)
            }
        }

        /**
         * Retrieves a node from the graph builder and casts it to [FullyQualifiedNodeBuilder].
         *
         * @param graph The [GraphBuilder] instance containing the nodes.
         * @param id The ID of the node to retrieve.
         * @return The [FullyQualifiedNodeBuilder] instance for the given ID.
         * @throws ClassCastException if the node with the given ID is not a [FullyQualifiedNodeBuilder].
         * @throws NullPointerException if the node with the given ID does not exist.
         */
        private fun getNode(graph: GraphBuilder, id: String): FullyQualifiedNodeBuilder {
            return graph.nodes[id] as FullyQualifiedNodeBuilder
        }
    }
}