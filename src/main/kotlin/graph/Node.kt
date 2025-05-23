package software.bevel.graph_domain.graph

import software.bevel.file_system_domain.LCRange

/**
 * Represents the different types of nodes that can exist in a code graph.
 */
enum class NodeType {
    /** Represents a file. */
    File,
    /** Represents a module, a collection of source files. */
    Module,
    /** Represents a namespace, a declarative region for identifiers. */
    Namespace,
    /** Represents a package, a way to organize related classes and interfaces. */
    Package,
    /** Represents a class definition. */
    Class,
    /** Represents a method within a class or object. */
    Method,
    /** Represents a property of a class, often with a getter and/or setter. */
    Property,
    /** Represents a field, typically a member variable of a class. */
    Field,
    /** Represents a constructor of a class. */
    Constructor,
    /** Represents an enumeration type. */
    Enum,
    /** Represents an interface definition. */
    Interface,
    /** Represents a standalone function. */
    Function,
    /** Represents a variable declaration. */
    Variable,
    /** Represents a constant value. */
    Constant,
    /** Represents a string literal or type. */
    String,
    /** Represents a numeric literal or type. */
    Number,
    /** Represents a boolean literal or type. */
    Boolean,
    /** Represents an array type or literal. */
    Array,
    /** Represents an object instance or type, or a companion object. */
    Object,
    /** Represents a key, typically in a map or JSON-like structure. */
    Key,
    /** Represents a null value or type. */
    Null,
    /** Represents a member of an enumeration. */
    EnumMember,
    /** Represents a struct or record type. */
    Struct,
    /** Represents an event. */
    Event,
    /** Represents an operator. */
    Operator,
    /** Represents a type parameter, often used in generics. */
    TypeParameter,
    /** Represents a type alias. */
    Alias;

    /**
     * Checks if this node type is function-like (e.g., Function, Method, Constructor).
     * @return `true` if the node type is function-like, `false` otherwise.
     */
    fun isFunctionLike(): kotlin.Boolean {
        return this == Function || this == Method || this == Constructor
    }

    /**
     * Checks if this node type is variable-like (e.g., Variable, Field, Property, Object).
     * @return `true` if the node type is variable-like, `false` otherwise.
     */
    fun isVariableLike(): kotlin.Boolean {
        return this == Variable || this == Field || this == Property || this == Object
    }

    /**
     * Checks if this node type is class-like (e.g., Class, Struct, Interface, Enum, Object).
     * @return `true` if the node type is class-like, `false` otherwise.
     */
    fun isClassLike(): kotlin.Boolean {
        return this == Class || this == Struct || this == Interface || this == Enum || this == Object
    }
}

/**
 * Represents a node in the code graph.
 *
 * @property id The unique identifier for this node. Typically a fully qualified name or a generated ID.
 * @property simpleName The simple, unqualified name of the node (e.g., "MyClass", "myFunction").
 * @property nodeType The [NodeType] of this node.
 * @property description A human-readable description or documentation for this node. Defaults to an empty string.
 * @property inboundConnectionVersion Version identifier for the state of inbound connections. Defaults to "Not generated".
 * @property outboundConnectionVersion Version identifier for the state of outbound connections. Defaults to "Not generated".
 * @property definingNodeName The name of the node that defines this node (e.g., the class name for a method node).
 * @property filePath The absolute path to the file where this node is defined.
 * @property codeLocation The [LCRange] (line and character range) spanning the entire code of this node in its file.
 * @property nameLocation The [LCRange] (line and character range) for the name/identifier of this node in its file.
 * @property codeHash A hash representing the content of the node's code, used for similarity comparisons.
 * @property nodeSignature A string representation of the node's signature as it appears in the code,
 *                         normalized with single spaces. This helps in identifying the node uniquely based on its
 *                         declaration characteristics (name, parameters, etc.). Defaults to `simpleName`.
 */
data class Node(
    val id: String,
    val simpleName: String,
    val nodeType: NodeType,
    val description: String = "",
    val inboundConnectionVersion: String = "Not generated",
    val outboundConnectionVersion: String = "Not generated",
    val definingNodeName: String,
    val filePath: String,
    val codeLocation: LCRange,
    val nameLocation: LCRange,
    val codeHash: String,
    val nodeSignature: String = simpleName,
)