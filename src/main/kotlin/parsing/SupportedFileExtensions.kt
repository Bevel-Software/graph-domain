package software.bevel.graph_domain.parsing

/**
 * A data class that holds a collection of supported file extensions, potentially categorized.
 * This can be used by parsers or other tools to identify relevant files for processing.
 *
 * @property supportedFileExtensions A map where keys might represent language names or categories (e.g., "Kotlin", "Java", "Text"),
 *                                   and values are lists of corresponding file extensions (e.g., `listOf(".kt", ".kts")`).
 *                                   Extensions should typically include the leading dot.
 */
data class SupportedFileExtensions(
    val supportedFileExtensions: Map<String, List<String>>
)