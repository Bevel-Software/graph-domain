package software.bevel.graph_domain.parsing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.bevel.graph_domain.graph.builder.GraphBuilder

/**
 * Defines a contract for parsers that process source files and convert them into an intermediate
 * graph representation, specifically a [GraphBuilder] instance.
 * This interface is typically implemented for different programming languages or file types.
 */
interface IntermediateFileParser {

    /**
     * Provides a [Logger] instance for logging messages related to the parsing process.
     * Defaults to a logger associated with the [IntermediateFileParser] class.
     */
    val logger: Logger
        get() = LoggerFactory.getLogger(IntermediateFileParser::class.java)

    /**
     * Parses a single file and returns a GraphBuilder.
     *
     * @param pathToFile the path to the Kotlin file.
     * @return a GraphBuilder.
     */
    fun parseFile(pathToFile: String): GraphBuilder

    /**
     * Parses a list of files and merges their results into a single [GraphBuilder].
     * This default implementation iterates through the provided list of `files`,
     * attempts to parse each one using [parseFile], and logs any errors encountered.
     * Successfully parsed graphs are then folded (merged) into a cumulative [GraphBuilder].
     *
     * @param files A list of file paths to parse.
     * @return A [GraphBuilder] instance containing the combined graph data from all successfully parsed files.
     */
    fun parseFiles(files: List<String>): GraphBuilder {
        return files.mapNotNull { file ->
            logger.info("Parsing: $file")
            try {
                parseFile(file)
            } catch (e: Exception) {
                logger.error("Failed to parse file: $file: \n$e\n${e.stackTraceToString()}")
                null
            }
        }.fold(GraphBuilder(mutableMapOf())) { acc, graph ->
            acc.addAll(graph)
            acc
        }
    }
}