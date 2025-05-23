package software.bevel.graph_domain.graph

import software.bevel.file_system_domain.LCRange

/**
 * Represents the type of relationship between two nodes in a graph.
 */
enum class ConnectionType {
    /** Indicates that the source node is a parent of the target node (e.g., a class containing a method). */
    PARENT_OF,
    /** Indicates that the source node inherits from the target node (e.g., a class extending another class). */
    INHERITS_FROM,
    /** Indicates that the source node uses the target node (e.g., a function using a class). */
    USES,
    /** Indicates that the source node is of the type defined by the target node (e.g., a variable of a certain class type). */
    IS_OF_TYPE,
    /** Indicates that the source node (e.g., a method) is invoked by the target node. */
    INVOKED_BY,
    /** Indicates that the source node (e.g., a function call) calls the target node (e.g., a function) with specific arguments. */
    CALLED_WITH,
    /** Indicates that the source node (e.g., a method) is overloaded by the target node (another method with the same name but different parameters). */
    OVERLOADED_BY,
    /** Indicates that the source node (e.g., a method in a subclass) overrides the target node (a method in a superclass). */
    OVERRIDES,
    /** Indicates that the source node (e.g., a class or file) defines the target node (e.g., a method or variable). */
    DEFINES
}

/**
 * Represents a directed connection between two nodes in a graph.
 *
 * @property sourceNodeName The unique identifier (name) of the source node of the connection.
 * @property targetNodeName The unique identifier (name) of the target node of the connection.
 * @property connectionType The [ConnectionType] describing the nature of the relationship.
 * @property filePath The absolute path to the file where this connection is defined or observed.
 * @property location The [LCRange] (line and character range) in the file where this connection occurs.
 */
data class Connection(
    val sourceNodeName: String,
    val targetNodeName: String,
    val connectionType: ConnectionType,
    val filePath: String,
    val location: LCRange,
)