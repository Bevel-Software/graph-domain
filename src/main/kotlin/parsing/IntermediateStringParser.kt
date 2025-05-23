package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.builder.GraphBuilder

/**
 * Defines a contract for parsers that process a string of text (e.g., source code content)
 * and convert it into an intermediate graph representation, specifically a [GraphBuilder] instance.
 * This is useful for parsing code snippets or content not directly read from a file.
 */
interface IntermediateStringParser {
    /**
     * Parses a given string of text and constructs a [GraphBuilder].
     *
     * @param text The string content to parse (e.g., a snippet of code).
     * @param filePath The original file path associated with this text, used for context (e.g., resolving relative imports or naming nodes).
     * @param startIndex The starting index or line number within the original file from which this `text` snippet originates.
     *                   This can be used for accurate location tracking of parsed elements.
     * @return A [GraphBuilder] instance representing the parsed content.
     */
    fun parseString(text: String, filePath: String, startIndex: Int): GraphBuilder
}