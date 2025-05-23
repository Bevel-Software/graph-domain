package software.bevel.graph_domain.graph.builder

import software.bevel.file_system_domain.LCRange
import software.bevel.graph_domain.graph.Node
import software.bevel.graph_domain.graph.NodeType

/**
 * A mutable builder for creating [Node] instances that are fully qualified.
 * This builder holds all the properties required to construct a [Node] and implements [NodeBuilder].
 *
 * @property id The unique identifier for this node. Typically a fully qualified name.
 * @property simpleName The simple, unqualified name of the node.
 * @property nodeType The [NodeType] of this node.
 * @property description A human-readable description or documentation for this node. Defaults to an empty string.
 * @property inboundConnectionVersion Version identifier for the state of inbound connections. Defaults to "Not generated".
 * @property outboundConnectionVersion Version identifier for the state of outbound connections. Defaults to "Not generated".
 * @property definingNodeName The name of the node that defines this node (e.g., the class name for a method node).
 * @property filePath The absolute path to the file where this node is defined.
 * @property codeLocation The [LCRange] (line and character range) spanning the entire code of this node.
 * @property nameLocation The [LCRange] (line and character range) for the name/identifier of this node.
 * @property codeHash A hash representing the content of the node's code.
 * @property nodeSignature A string representation of the node's signature. Defaults to `simpleName`.
 */
data class FullyQualifiedNodeBuilder(
    override var id: String,
    var simpleName: String,
    var nodeType: NodeType,
    var description: String = "",
    var inboundConnectionVersion: String = "Not generated",
    var outboundConnectionVersion: String = "Not generated",
    var definingNodeName: String,
    var filePath: String,
    val codeLocation: LCRange,
    val nameLocation: LCRange,
    var codeHash: String,
    var nodeSignature: String = simpleName,
): NodeBuilder {
    /**
     * Secondary constructor that initializes the builder from an existing [Node] instance.
     *
     * @param node The [Node] instance to copy properties from.
     */
    constructor(node: Node) : this(
        node.id,
        node.simpleName,
        node.nodeType,
        node.description,
        node.inboundConnectionVersion,
        node.outboundConnectionVersion,
        node.definingNodeName,
        node.filePath,
        node.codeLocation,
        node.nameLocation,
        node.codeHash,
        node.nodeSignature
    )

    /**
     * Builds an immutable [Node] instance from the current state of this builder.
     *
     * @return A new [Node] instance.
     */
    fun build(): Node {
        return Node(
            this.id,
            simpleName,
            nodeType,
            description,
            inboundConnectionVersion,
            outboundConnectionVersion,
            definingNodeName,
            filePath,
            codeLocation,
            nameLocation,
            codeHash,
            nodeSignature
        )
    }
}