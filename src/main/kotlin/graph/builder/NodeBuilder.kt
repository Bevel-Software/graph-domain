package software.bevel.graph_domain.graph.builder

/**
 * Defines the basic contract for a node builder within a graph construction process.
 * A node builder is responsible for providing an identifier for the node it represents or builds.
 */
interface NodeBuilder{
    /**
     * The unique identifier for the node being built or represented.
     * This ID is used to reference the node within the graph structure.
     */
    val id: String
}