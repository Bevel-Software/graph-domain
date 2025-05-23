package software.bevel.graph_domain.tokenizers

import software.bevel.graph_domain.parsing.PotentialSymbol
import java.util.HashSet

/**
 * Defines a contract for tokenizers that specifically focus on extracting identifiers from text.
 * Implementations are expected to use regular expressions and a list of reserved words to distinguish
 * meaningful identifiers from other language constructs or keywords.
 */
interface IdentifierTokenizer {
    /**
     * Tokenizes the given input string to extract potential identifiers.
     *
     * This method should apply the `wordRegex` to the `input` string to find all matching sequences (words).
     * Each found word is then checked against the `reservedWordsSet`. If a word is not in the reserved set,
     * it is considered a potential identifier, and a [PotentialSymbol] object is created for it,
     * capturing its name, the `relativePath` of the source, and its start/end positions within the `input`.
     *
     * @param wordRegex A [Regex] used to identify word-like sequences in the input text.
     * @param reservedWordsSet A [HashSet] of strings containing words that should be ignored (e.g., language keywords).
     * @param relativePath The relative path of the file or source from which the `input` text originates. Used for context in [PotentialSymbol].
     * @param input The string of text to tokenize.
     * @return A list of [PotentialSymbol] objects, each representing an identified non-reserved identifier found in the input.
     */
    fun tokenize(wordRegex: Regex, reservedWordsSet: HashSet<String>, relativePath: String, input: String): List<PotentialSymbol>
}