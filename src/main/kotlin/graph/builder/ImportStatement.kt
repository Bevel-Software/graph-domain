package software.bevel.graph_domain.graph.builder

/**
 * Represents an import statement found in source code.
 *
 * @property importedPackage The fully qualified name of the package or module being imported (e.g., "java.util", "com.example.mymodule").
 * @property data A map containing details about the import. The keys and values can vary depending on the language and parser.
 *               For example, it might store aliases if the import uses one (e.g., `"MyClass" to "com.example.MyClass"` if aliased,
 *               or `"SpecificClass" to "SpecificClass"` for a direct import from the package).
 *               If it's a wildcard import (e.g., `import java.util.*`), the map might be empty or contain a special marker.
 */
class ImportStatement(
    val importedPackage: String,
    val data: Map<String, String>,
)

///**
// * Original type alias for an import statement, potentially representing a simpler structure
// * where the map directly held imported names to their fully qualified names or aliases.
// * This has been replaced by the [ImportStatement] class for better structure and clarity.
// */
//typealias ImportStatement = Map<String, String>