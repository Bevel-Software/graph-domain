package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.builder.GraphBuilder

interface IntermediateStringParser {
    fun parseString(text: String, filePath: String, startIndex: Int): GraphBuilder
}