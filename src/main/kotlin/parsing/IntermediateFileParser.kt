package software.bevel.graph_domain.parsing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.bevel.graph_domain.graph.builder.GraphBuilder

interface IntermediateFileParser {

    val logger: Logger
        get() = LoggerFactory.getLogger(IntermediateFileParser::class.java)

    /**
     * Parses a single file and returns a GraphBuilder.
     *
     * @param pathToFile the path to the Kotlin file.
     * @return a GraphBuilder.
     */
    fun parseFile(pathToFile: String): GraphBuilder

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