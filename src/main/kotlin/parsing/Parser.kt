package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.Graphlike
import software.bevel.graph_domain.graph.builder.GraphBuilder

/**
 * Defines a high-level contract for parsing one or more projects (specified by paths)
 * and constructing a graph representation from them.
 * Implementations are responsible for orchestrating the parsing of all relevant files within the projects.
 */
interface Parser {

    /**
     * Parses the projects specified by `pathsToProjects` and builds a complete [Graphlike] representation.
     * The default implementation first calls [parseToGraphBuilder] to get an intermediate [GraphBuilder],
     * then builds the final graph using the first project path as a potential root or primary identifier.
     *
     * @param pathsToProjects A list of paths to the projects to be parsed. The first path might be treated as the primary project path.
     * @return A [Graphlike] instance representing the combined graph of all parsed projects.
     */
    fun parse(pathsToProjects: List<String>): Graphlike {
        return parseToGraphBuilder(pathsToProjects).build(pathsToProjects[0])
    }

    /**
     * Parses the projects specified by `pathsToProjects` into an intermediate [GraphBuilder] representation.
     * This method is responsible for processing all relevant source files within the given project paths
     * and populating a [GraphBuilder] with the extracted nodes and connections.
     *
     * @param pathsToProjects A list of paths to the projects to be parsed.
     * @return A [GraphBuilder] instance containing the raw graph data from the parsed projects.
     */
    fun parseToGraphBuilder(pathsToProjects: List<String>): GraphBuilder
}