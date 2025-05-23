package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.Graphlike
import software.bevel.graph_domain.graph.builder.GraphBuilder

interface Parser {

    fun parse(pathsToProjects: List<String>): Graphlike {
        return parseToGraphBuilder(pathsToProjects).build(pathsToProjects[0])
    }

    fun parseToGraphBuilder(pathsToProjects: List<String>): GraphBuilder
}