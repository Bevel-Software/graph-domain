package software.bevel.graph_domain.graph.builder

import software.bevel.graph_domain.graph.*

/**
 * A builder class for constructing [Graphlike] objects.
 * It manages a collection of [NodeBuilder]s and [ConnectionsBuilder] to assemble a graph.
 * Can be hierarchical with a parent [GraphBuilder].
 *
 * @property nodes A mutable map of node IDs to [NodeBuilder] instances.
 * @property parent An optional parent [GraphBuilder], allowing for hierarchical graph construction.
 * @property connectionsBuilder The [ConnectionsBuilder] used to manage connections within this graph.
 */
open class GraphBuilder(
    var nodes: MutableMap<String, NodeBuilder>,
    var parent: GraphBuilder? = null,
    val connectionsBuilder: ConnectionsBuilder = MutableMapConnectionsBuilder(),
) {

    /**
     * Secondary constructor that initializes the [GraphBuilder] from an existing [Graphlike] instance.
     * Nodes from the input graph are converted to [FullyQualifiedNodeBuilder]s.
     *
     * @param graph The [Graphlike] instance to initialize from.
     */
    constructor(graph: Graphlike) : this(
        graph.nodes.mapValues { FullyQualifiedNodeBuilder(it.value) }.toMutableMap(),
        null,
        MutableMapConnectionsBuilder(graph.connections)
    )

    /**
     * Adds a connection and ensures that its source and target nodes exist in the builder.
     * If nodes are missing, [DanglingNodeBuilder]s are created for them using the connection's file path and the provided context.
     *
     * @param connection The [Connection] to add.
     * @param context An optional [NodeBuilder] representing the context in which this connection is being added.
     */
    fun addConnectionAndMissingNodes(connection: Connection, context: NodeBuilder?) {
        addConnectionAndMissingNodes(
            connection,
            { DanglingNodeBuilder(connection.sourceNodeName, connection.filePath, context = context) },
            { DanglingNodeBuilder(connection.targetNodeName, connection.filePath, context = context) },
        )
    }

    /**
     * Adds a connection and ensures that its source and target nodes exist in the builder.
     * If nodes are missing, this method uses the provided fallback functions to create them, or defaults to [DanglingNodeBuilder].
     *
     * @param connection The [Connection] to add.
     * @param sourceNodeFallback An optional function to create a [NodeBuilder] for the source node if it's missing. Defaults to creating a [DanglingNodeBuilder].
     * @param targetNodeFallback An optional function to create a [NodeBuilder] for the target node if it's missing. Defaults to creating a [DanglingNodeBuilder].
     * @param context An optional [NodeBuilder] representing the context, used if default [DanglingNodeBuilder]s are created.
     */
    fun addConnectionAndMissingNodes(connection: Connection, sourceNodeFallback: (() -> NodeBuilder)? = null, targetNodeFallback: (() -> NodeBuilder)? = null, context: NodeBuilder? = null) {
        var sourceName = connection.sourceNodeName
        var targetName = connection.targetNodeName
        if(!nodes.containsKey(connection.sourceNodeName)) {
            val newNode = if (sourceNodeFallback == null) {
                DanglingNodeBuilder(connection.sourceNodeName, connection.filePath, context = context)
            } else {
                sourceNodeFallback()
            }
            if(!nodes.containsKey(newNode.id)) {
                nodes[newNode.id] = newNode
            }
            sourceName = newNode.id
        }
        if(!nodes.containsKey(connection.targetNodeName)) {
            val newNode = if (targetNodeFallback == null) {
                DanglingNodeBuilder(connection.targetNodeName, connection.filePath, context = context)
            } else {
                targetNodeFallback()
            }
            if(!nodes.containsKey(newNode.id)) {
                nodes[newNode.id] = newNode
            }
            targetName = newNode.id
        }
        val con = connection.copy(sourceNodeName = sourceName, targetNodeName = targetName)
        this.connectionsBuilder.addConnection(con)
    }

    /**
     * Builds an immutable [Graphlike] instance from the current state of the builder.
     * Only [FullyQualifiedNodeBuilder] instances are converted to [Node]s in the final graph.
     *
     * @param projectPath An optional project path string, currently not used in this build implementation but available for future extensions.
     * @return A new [Graphlike] instance.
     */
    fun build(projectPath: String? = null): Graphlike {
        return Graph(nodes.filter { it.value is FullyQualifiedNodeBuilder }
            .mapValues { it.value as FullyQualifiedNodeBuilder }
            .mapValues { nodeKV -> nodeKV.value.build() }, this.connectionsBuilder.build())
    }

    /**
     * Merges connections from one node (`fromId`) into another node (`intoId`).
     * Delegates to [ConnectionsBuilder.mergeNodes].
     *
     * @param intoId The ID of the node to merge connections into.
     * @param fromId The ID of the node whose connections are to be merged from.
     */
    fun mergeNode(intoId: String, fromId: String) {
        connectionsBuilder.mergeNodes(intoId, fromId)
    }

    /**
     * Deletes a node and all connections related to it.
     *
     * @param nodeId The ID of the node to delete.
     */
    fun deleteNode(nodeId: String) {
        connectionsBuilder.deleteConnectionsRelatedToNode(nodeId)
        nodes.remove(nodeId)
    }

    /**
     * Adds all nodes and connections from another [GraphBuilder] into this one.
     * If a node ID already exists, the existing node is kept (behavior for merging node details might need refinement, see TODO).
     *
     * @param other The [GraphBuilder] instance to merge from.
     */
    open fun addAll(other: GraphBuilder) {
        other.nodes.forEach {
            if(!nodes.containsKey(it.key)) {
                //TODO: check for adding to map twice
                nodes[it.key] = it.value
            } else {
                //println("Duplicate node merged, ${it.key}: into - ${nodes[it.key]} , from - ${it.value} ${it.value is FullyQualifiedNodeBuilder}")
            }
        }
        connectionsBuilder.addAll(other.connectionsBuilder)

        // This line seems to overwrite previous logic if nodes had same keys, effectively taking `other.nodes` values for duplicates.
        nodes.putAll(other.nodes)
    }

    /**
     * Retrieves child nodes of a given node.
     * Children are determined by [ConnectionType.PARENT_OF] connections where the given node is the source,
     * and also by [FullyQualifiedNodeBuilder]s whose `definingNodeName` matches the given `nodeId`.
     *
     * @param nodeId The ID of the parent node.
     * @return A list of child [NodeBuilder] instances.
     */
    fun getChildren(nodeId: String): List<NodeBuilder> {
        return this.connectionsBuilder.findConnectionsFrom(nodeId)
            .filter { it.connectionType == ConnectionType.PARENT_OF }
            .mapNotNull {
                nodes[it.targetNodeName]
            } + this.nodes.values.filterIsInstance<FullyQualifiedNodeBuilder>().filter { it.definingNodeName == nodeId }
    }

    /**
     * Retrieves the parent node of a given node.
     * A parent is first sought through a [ConnectionType.PARENT_OF] connection where the given node is the target.
     * If not found, it falls back to calling [getDefiner].
     *
     * @param nodeId The ID of the child node.
     * @return The parent [NodeBuilder] instance, or `null` if not found.
     */
    fun getParent(nodeId: String): NodeBuilder? {
        return this.connectionsBuilder.findConnectionsToOfType(nodeId, ConnectionType.PARENT_OF).firstOrNull()
            ?.let { nodes[it.sourceNodeName] }
            ?: getDefiner(nodeId)
    }

    /**
     * Retrieves the "definer" of a node. 
     * In the current implementation, this method returns the node itself if it exists in the `nodes` map.
     * If the node is a [DanglingNodeBuilder] with a context, and not found directly by `nodeId`, it returns its context.
     * Otherwise, it returns `null`.
     * Note: The primary behavior `return this.nodes[nodeId]` means it often returns the input node itself, which might not align with typical expectations of a "getDefiner" method.
     *
     * @param nodeId The ID of the node whose definer is sought.
     * @return The defining [NodeBuilder] or the node itself, or `null`.
     */
    fun getDefiner(nodeId: String): NodeBuilder? {
        val node = nodes[nodeId]

        return this.nodes[nodeId]
            ?: if (node is DanglingNodeBuilder && node.context != null) node.context else null
    }

    /**
     * Retrieves the invoker of a given node.
     * An invoker is determined by a [ConnectionType.INVOKED_BY] connection where the given node is the target (i.e., `sourceNodeName` is the invoker).
     *
     * @param nodeId The ID of the node that is invoked.
     * @return The invoking [NodeBuilder] instance, or `null` if not found.
     */
    fun getInvoker(nodeId: String): NodeBuilder? {
        return this.connectionsBuilder.findConnectionsFromOfType(nodeId, ConnectionType.INVOKED_BY).firstOrNull()
            ?.let { nodes[it.sourceNodeName] } 
    }

    /**
     * Retrieves the supertypes of a given node.
     * Supertypes are determined by [ConnectionType.INHERITS_FROM] connections where the given node is the source.
     *
     * @param nodeId The ID of the node whose supertypes are sought.
     * @return A list of supertype [NodeBuilder] instances.
     */
    fun getSuperType(nodeId: String): List<NodeBuilder> {
        return this.connectionsBuilder.findConnectionsFromOfType(nodeId, ConnectionType.INHERITS_FROM)
            .mapNotNull { nodes[it.targetNodeName] }
    }
}
