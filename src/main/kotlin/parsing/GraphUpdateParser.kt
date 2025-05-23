package software.bevel.graph_domain.parsing

import software.bevel.graph_domain.graph.Graphlike

/**
 * Defines a contract for parsers that can update an existing [Graphlike] structure
 * based on changes to a set of source files.
 * This typically involves reparsing modified files, removing data related to deleted files,
 * and adding data from new files.
 */
interface GraphUpdateParser {
    /**
     * Reparses a list of specified files and updates the `currentGraph` accordingly.
     * This is used when existing files have been modified and their representation in the graph needs to be refreshed.
     *
     * @param filesToUpdate A list of file paths or identifiers that have been updated and need reparsing.
     * @param currentGraph The current [Graphlike] instance to be updated.
     * @return A [Graphlike] instance reflecting the changes after reparsing the files.
     */
    fun reparseFiles(filesToUpdate: List<String>, currentGraph: Graphlike): Graphlike

    /**
     * Deletes information related to a list of specified files from the `currentGraph`.
     * This is used when files have been removed from the project and their corresponding nodes and connections
     * should be removed from the graph.
     *
     * @param filesToDelete A list of file paths or identifiers that have been deleted.
     * @param currentGraph The current [Graphlike] instance to be updated.
     * @return A [Graphlike] instance reflecting the state after removing data related to the deleted files.
     */
    fun deleteFiles(filesToDelete: List<String>, currentGraph: Graphlike): Graphlike

    /**
     * Parses a list of newly added files and incorporates their information into the `currentGraph`.
     * This is used when new files are introduced to the project.
     *
     * @param filesToAdd A list of file paths or identifiers for new files to be parsed and added to the graph.
     * @param currentGraph The current [Graphlike] instance to be updated.
     * @return A [Graphlike] instance reflecting the state after adding data from the new files.
     */
    fun addFiles(filesToAdd: List<String>, currentGraph: Graphlike): Graphlike
}