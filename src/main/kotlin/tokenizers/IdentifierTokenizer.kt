package software.bevel.graph_domain.tokenizers

import software.bevel.graph_domain.parsing.PotentialSymbol
import java.util.HashSet

interface IdentifierTokenizer {
    fun tokenize(wordRegex: Regex, reservedWordsSet: HashSet<String>, relativePath: String, input: String): List<PotentialSymbol>
}