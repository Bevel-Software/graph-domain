package software.bevel.graph_domain.graph.builder

import software.bevel.graph_domain.graph.NodeType

/**
 * Represents a builder for a node that is potentially "dangling" or unresolved.
 * This is often used for nodes encountered during parsing that cannot be fully qualified
 * or linked to a concrete definition at the time of their discovery, such as an unresolved import
 * or a type mentioned in a file before its full definition is processed.
 *
 * @property name The simple name of the dangling node.
 * @property filePath The path to the file where this dangling node is referenced.
 * @property importStatements A list of [ImportStatement]s relevant to resolving this node. Defaults to an empty list.
 * @property context An optional [NodeBuilder] representing the context in which this dangling node appears (e.g., the class or function it's mentioned in). Defaults to null.
 * @property nodeType An optional [NodeType] indicating the presumed type of the dangling node, if known. Defaults to null.
 */
class DanglingNodeBuilder(
    val name: String,
    val filePath: String,
    var importStatements: List<ImportStatement> = listOf(),
    val context: NodeBuilder? = null,
    val nodeType: NodeType? = null,
): NodeBuilder {

    /**
     * The unique identifier for this dangling node.
     * It's constructed by appending context information to the [name] if a [context] is provided.
     * For example, if name is "MyType" and context id is "com.example.MyClass", the id might be "MyType from com.example.MyClass".
     * Note: The original implementation `get() = id + ...` would lead to a StackOverflowError due to recursion.
     * It's assumed to be `get() = name + ...` or similar.
     */
    override val id: String
        get() = name + (if (context != null) " from " + context.id else "")
}