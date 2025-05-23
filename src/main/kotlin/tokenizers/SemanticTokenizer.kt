package software.bevel.graph_domain.tokenizers

interface SemanticTokenizer {
    fun tokenizeCode(input: String): List<String>
}