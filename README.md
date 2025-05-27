# Graph Domain (Kotlin Library for Bevel)

[![Maven Central](https://img.shields.io/maven-central/v/software.bevel/graph-domain.svg?label=Maven%20Central)]([https://search.maven.org/search?q=g:%22software.bevel%22%20AND%20a:%22graph-domain%22](https://central.sonatype.com/artifact/software.bevel/graph-domain))
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)
<!-- TODO: Add Build Status Badge once CI is set up -->
<!-- [![Build Status](https://github.com/Bevel-Software/graph-domain/actions/workflows/your-ci-workflow.yml/badge.svg)](https://github.com/Bevel-Software/graph-domain/actions/workflows/your-ci-workflow.yml) -->

## Introduction

`graph-domain` is a Kotlin/JVM library, part of the Bevel suite of developer tools. Bevel's primary goal is to extract comprehensive knowledge graphs from codebases. This library provides the core domain model, data structures, and services necessary for representing, constructing, manipulating, and analyzing these code-derived graphs.

It defines entities like `Node` (representing code elements such as classes, functions, variables) and `Connection` (representing relationships like inheritance, usage, calls), along with services for parsing source code into these graph structures, merging different graph versions, and more.

## Relationship to Bevel and `file-system-domain`

The Bevel ecosystem is designed to understand and process software projects.
*   The [`file-system-domain`](https://github.com/Bevel-Software/file-system-domain) library (another Bevel component) focuses on standardized file system interactions, path resolution, and handling Bevel-specific project layouts.
*   `graph-domain` builds upon this by focusing on the *content* of the files within a project. It provides the tools to parse source code, represent its structure and semantics as a graph, and perform operations on this graph data.

Together, these libraries form a foundation for building advanced code analysis, visualization, and comprehension tools under the Bevel umbrella.

## Project Overview & Core Concepts

`graph-domain` offers a rich set of tools and abstractions for working with code graphs:

*   **Graph Representation**:
    *   `Node`: Represents a code element (e.g., class, method, variable, file). Each node has a `NodeType`, unique `id`, `simpleName`, `description`, location information (`LCRange` from `file-system-domain`), `codeHash`, and `nodeSignature`.
    *   `Connection`: Represents a directed relationship between two nodes (e.g., `USES`, `INHERITS_FROM`, `PARENT_OF`). Each connection has a `ConnectionType`, source/target node names, and location.
    *   `Graphlike` & `Graph`: Interfaces and classes for representing the entire graph structure, holding collections of nodes and connections.
    *   `ConnectionsNavigator` & `ConnectionsBuilder`: Interfaces for querying and constructing sets of connections, with implementations like `MutableMapConnectionsBuilder`.

*   **Graph Construction**:
    *   `GraphBuilder`: A central class for assembling `Graphlike` objects from various sources. Manages `NodeBuilder` instances and connections.
    *   `NodeBuilder` (e.g., `FullyQualifiedNodeBuilder`, `DanglingNodeBuilder`): Used during the graph construction phase to represent nodes before they are finalized. `DanglingNodeBuilder` is particularly useful for handling unresolved references during parsing.

*   **Parsing Abstractions**:
    *   `Parser`: A high-level interface for parsing entire projects (potentially multi-language) into a `Graphlike` structure.
    *   `IntermediateFileParser` & `IntermediateStringParser`: Interfaces for language-specific parsers that transform file content or raw strings into an intermediate `GraphBuilder` representation.
    *   `ConnectionParser`: Interface for parsers that establish connections (e.g., call graphs, inheritance) between already identified nodes.
    *   `GraphUpdateParser`: Interface for parsers that can incrementally update an existing graph based on file changes (additions, modifications, deletions).
    *   `LanguageSpecification`: Defines language-specific properties like supported file extensions.

*   **Graph Merging**:
    *   `GraphMergingService` (`GraphMergingServiceImpl`): Provides sophisticated logic to merge two graph structures. This includes:
        *   Matching nodes based on ID, file path + signature, file path + simple name, or code similarity (using `LocalitySensitiveHasher`).
        *   Merging node descriptions, with mechanisms to flag conflicts or outdated information (e.g., `[OUTDATED] [NEEDS MERGE]`).
        *   Combining connections intelligently.

*   **Tokenization**:
    *   `SemanticTokenizer` (e.g., `CharSemanticTokenizer`): For breaking down code into meaningful tokens (keywords, identifiers, operators).
    *   `IdentifierTokenizer` (e.g., `RegexIdentifierTokenizer`): For extracting potential symbols/identifiers from code, along with their locations (`PotentialSymbol`).

*   **Code Hashing & Similarity**:
    *   `LocalitySensitiveHasher`: Interface for LSH algorithms, used by the `GraphMergingService` to compare code snippets for similarity when exact matches are not found.

*   **Graph Metrics**:
    *   `Metrics`: A utility class to compute and log various statistics about a graph, such as the number of nodes, connections, and types of nodes.

## Key Features

*   🔷 **Comprehensive Code Graph Model**: Rich data structures (`Node`, `Connection`, `NodeType`, `ConnectionType`) for accurately representing diverse code elements and their relationships.
*   🛠️ **Flexible Graph Construction**: Builder patterns (`GraphBuilder`, `NodeBuilder`) for programmatic and step-by-step graph creation.
*   🔄 **Advanced Graph Merging**: Sophisticated service (`GraphMergingService`) to combine graph data from different sources or versions, including conflict resolution for descriptions and intelligent node matching.
*   🧩 **Abstracted Parsing Framework**: A suite of parsing interfaces (`Parser`, `IntermediateFileParser`, `ConnectionParser`) designed for extensibility, allowing support for various programming languages and custom parsing logic.
*   📈 **Incremental Graph Updates**: Support for updating graphs based on file system changes (`GraphUpdateParser`), essential for analyzing evolving codebases.
*   🔍 **Semantic & Identifier Tokenization**: Tools to break down source code into tokens and identify key symbols.
*   ⚖️ **Code Similarity Analysis**: Utilizes Locality Sensitive Hashing (`LocalitySensitiveHasher`) for comparing code fragments, aiding in tasks like refactoring detection or duplicate code analysis.
*   📊 **Graph Metrics**: Utilities for calculating and reporting statistics on graph structures.
*   🔗 **Integration with `file-system-domain`**: Leverages `file-system-domain` for file path management and location tracking (e.g., `LCRange`, `LCPosition`).
*   📦 **Maven Central Availability**: Easy to integrate into any JVM project.

## Installation / Getting Started

### Prerequisites

*   Java Development Kit (JDK) 17 or higher.
*   A build tool like Gradle or Maven.

### Adding as a Dependency

The library is available on Maven Central.

**Gradle (Kotlin DSL - `build.gradle.kts`):**
```kotlin
dependencies {
    implementation("software.bevel:graph-domain:1.0.0")
    // You will also likely need file-system-domain
    implementation("software.bevel:file-system-domain:1.0.0")
}
```

**Gradle (Groovy DSL - `build.gradle`):**
```groovy
dependencies {
    implementation 'software.bevel:graph-domain:1.0.0'
    // You will also likely need file-system-domain
    implementation 'software.bevel:file-system-domain:1.0.0'
}
```

**Maven (`pom.xml`):**
```xml
<dependency>
    <groupId>software.bevel</groupId>
    <artifactId>graph-domain</artifactId>
    <version>1.0.0</version>
</dependency>
<!-- You will also likely need file-system-domain -->
<dependency>
    <groupId>software.bevel</groupId>
    <artifactId>file-system-domain</artifactId>
    <version>1.0.0</version>
</dependency>
```
*(Replace `1.0.0` with the latest version if necessary. Check Maven Central for the latest versions.)*

## Usage Instructions & API Highlights

This library provides a domain model and services. Actual usage will involve implementing some of its parser interfaces or using tools built on top of it.

### Core Data Structures:

*   **`software.bevel.graph_domain.graph.Node`**: Represents code elements.
    ```kotlin
    import software.bevel.graph_domain.graph.Node
    import software.bevel.graph_domain.graph.NodeType
    import software.bevel.file_system_domain.LCRange // From file-system-domain
    import software.bevel.file_system_domain.LCPosition // From file-system-domain

    val myClassNode = Node(
        id = "com.example.MyClass",
        simpleName = "MyClass",
        nodeType = NodeType.Class,
        description = "A sample class.",
        definingNodeName = "com.example", // package
        filePath = "/path/to/project/src/main/kotlin/com/example/MyClass.kt",
        codeLocation = LCRange(LCPosition(0,0), LCPosition(10,1)),
        nameLocation = LCRange(LCPosition(2,6), LCPosition(2,13)),
        codeHash = "abc123def456", // Example hash
        nodeSignature = "class MyClass"
    )
    ```

*   **`software.bevel.graph_domain.graph.Connection`**: Represents relationships.
    ```kotlin
    import software.bevel.graph_domain.graph.Connection
    import software.bevel.graph_domain.graph.ConnectionType
    import software.bevel.file_system_domain.LCRange
    import software.bevel.file_system_domain.LCPosition

    val usageConnection = Connection(
        sourceNodeName = "com.example.MyMethod",
        targetNodeName = "com.example.MyClass",
        connectionType = ConnectionType.USES,
        filePath = "/path/to/project/src/main/kotlin/com/example/MyMethodContainer.kt",
        location = LCRange(LCPosition(5,4), LCPosition(5,20))
    )
    ```

*   **`software.bevel.graph_domain.graph.Graphlike` / `software.bevel.graph_domain.graph.Graph`**: Represents the graph.
    ```kotlin
    import software.bevel.graph_domain.graph.Graph
    import software.bevel.graph_domain.graph.builder.MutableMapConnectionsBuilder

    val nodesMap = mapOf(myClassNode.id to myClassNode /*, ... other nodes */)
    val connectionsBuilder = MutableMapConnectionsBuilder()
    connectionsBuilder.addConnection(usageConnection)

    val myGraph = Graph(nodes = nodesMap, connections = connectionsBuilder.build())
    ```

### Key Services & Builders:

*   **`software.bevel.graph_domain.graph.builder.GraphBuilder`**: For programmatic construction.
    ```kotlin
    import software.bevel.graph_domain.graph.builder.GraphBuilder
    import software.bevel.graph_domain.graph.builder.FullyQualifiedNodeBuilder
    // ... other imports

    val graphBuilder = GraphBuilder(mutableMapOf())
    val nodeBuilder = FullyQualifiedNodeBuilder(/* ... node details ... */)
    graphBuilder.nodes[nodeBuilder.id] = nodeBuilder
    // graphBuilder.addConnectionAndMissingNodes(...)
    val finalGraph = graphBuilder.build()
    ```

*   **`software.bevel.graph_domain.GraphMergingService`**: Interface for merging graphs. Implementations like `GraphMergingServiceImpl` use `LocalitySensitiveHasher` and `FileHandler` (from `file-system-domain`).
    ```kotlin
    // Conceptual usage
    // val mergingService: GraphMergingService = GraphMergingServiceImpl(hasher, fileHandler)
    // val mergedGraph = mergingService.mergeNodeDescriptionsAndConnectionsFromOtherIntoCurrentGraph(
    //     currentGraph, otherGraph, "/path/to/project"
    // )
    ```

*   **`software.bevel.graph_domain.parsing.IntermediateFileParser`**: Implement this interface for each programming language you want to parse.
    ```kotlin
    // interface MyLangParser : IntermediateFileParser {
    //     override fun parseFile(pathToFile: String): GraphBuilder {
    //         // ... parsing logic for MyLang ...
    //         val builder = GraphBuilder(mutableMapOf())
    //         // Populate builder with nodes and connections from pathToFile
    //         return builder
    //     }
    // }
    ```

*   **`software.bevel.graph_domain.hashing.LocalitySensitiveHasher`**: Interface for code similarity hashing.

### Core Packages:
*   `software.bevel.graph_domain.graph`: Core graph data structures (`Node`, `Connection`, `Graphlike`).
*   `software.bevel.graph_domain.graph.builder`: Builders for graph elements (`GraphBuilder`, `NodeBuilder`, `ConnectionsBuilder`).
*   `software.bevel.graph_domain.parsing`: Interfaces for various parsing stages and language support (`Parser`, `IntermediateFileParser`, `GraphUpdateParser`).
*   `software.bevel.graph_domain.hashing`: `LocalitySensitiveHasher` interface.
*   `software.bevel.graph_domain.tokenizers`: Interfaces and implementations for code tokenization.
*   `software.bevel.graph_domain`: Top-level services like `GraphMergingService` and `Metrics`.


## Building from Source

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Bevel-Software/graph-domain.git # Or your fork's URL
    cd graph-domain
    ```

2.  **Build the project using Gradle:**
    ```bash
    ./gradlew build
    ```
    This will compile the source code, run tests, and build the JAR file (typically found in `build/libs/`).

3.  **Run tests:**
    ```bash
    ./gradlew test
    ```

## Dependencies

This project relies on several key dependencies:

*   **`software.bevel:file-system-domain`**: For file system utilities, path handling, and text location structures (`LCPosition`, `LCRange`).
*   **`org.slf4j:slf4j-api`**: A logging facade.
*   **`org.jetbrains.kotlin:kotlin-stdlib`**: The Kotlin standard library.
*   (Testing) `org.jetbrains.kotlin:kotlin-test`

Dependency licenses are detailed in the `NOTICE` file.

## Contributing

🎉 Contributions are welcome! We’re excited to collaborate with the community to improve `graph-domain`.

*   **Report Issues:** If you encounter a problem or have questions, please [open an issue](https://github.com/Bevel-Software/graph-domain/issues) (replace with actual issue tracker URL if different).
*   **Feature Requests:** Have an idea? Open an issue to discuss it.
*   **Pull Requests:**
    *   Fork the repository and create your branch from `main` (or the relevant development branch).
    *   Ensure tests pass (`./gradlew test`).
    *   If you add new functionality, please add tests for it.
    *   Follow Kotlin coding conventions.
    *   Make sure your code is formatted.

Please be respectful in all interactions.

## License

This project is open source and available under the [Mozilla Public License Version 2.0](LICENSE). See the `LICENSE` file for the full license text.

The `NOTICE` file contains information about licenses of third-party dependencies used in this project.

---

Happy coding! We hope `graph-domain` provides a solid foundation for your code analysis and knowledge extraction endeavors.
```
