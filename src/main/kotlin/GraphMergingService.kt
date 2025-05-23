package software.bevel.graph_domain

import software.bevel.graph_domain.graph.Graphlike

/**
 * Defines the contract for merging graph-like structures.
 */
interface GraphMergingService {
    /**
     * Merges node descriptions and connections from another graph into the current graph.
     *
     * @param currentGraph The graph to merge into.
     * @param otherGraph The graph to merge from.
     * @param projectPath The path of the project, used for context if needed during merging.
     * @return A new graph-like structure representing the merged result.
     */
    fun mergeNodeDescriptionsAndConnectionsFromOtherIntoCurrentGraph(currentGraph: Graphlike, otherGraph: Graphlike, projectPath: String): Graphlike
}