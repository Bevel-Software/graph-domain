package software.bevel.graph_domain.parsing

import java.util.*

/**
 * Defines a contract for specifying language-specific characteristics, primarily for parsing.
 * This includes details like supported file extensions.
 */
interface LanguageSpecification {
    /**
     * A list of file endings (extensions) that this language specification supports.
     * For example, for Kotlin, this might include ".kt", ".kts".
     * The extensions should typically include the leading dot.
     */
    var supportedFileEndings: List<String>

    /**
     * Checks if the given file name has an extension supported by this language specification.
     * The default implementation performs a case-insensitive check against the [supportedFileEndings] list.
     *
     * @param fileName The name of the file to check.
     * @return `true` if the file's extension is in the [supportedFileEndings] list, `false` otherwise.
     */
    fun checkFileEndings(fileName: String): Boolean {
        return supportedFileEndings.any { fileName.lowercase(Locale.getDefault()).endsWith(it) }
    }
}