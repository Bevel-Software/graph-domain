package software.bevel.graph_domain.parsing

import java.util.*

interface LanguageSpecification {
    var supportedFileEndings: List<String>

    fun checkFileEndings(fileName: String): Boolean {
        return supportedFileEndings.any { fileName.lowercase(Locale.getDefault()).endsWith(it) }
    }
}