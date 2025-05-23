package software.bevel.graph_domain.tokenizers

/**
 * Defines a contract for tokenizers that break down a string of code into a sequence of semantic tokens.
 * Implementations might use different strategies (e.g., character-based, regex-based, language-specific rules)
 * to achieve this tokenization.
 */
interface SemanticTokenizer {
    /**
     * Tokenizes the given input string into a list of semantic tokens.
     * Each token in the resulting list represents a meaningful unit of the input code,
     * such as an identifier, keyword, operator, or literal.
     *
     * @param input The string of code to be tokenized.
     * @return A list of strings, where each string is a semantic token.
     */
    fun tokenizeCode(input: String): List<String>
}