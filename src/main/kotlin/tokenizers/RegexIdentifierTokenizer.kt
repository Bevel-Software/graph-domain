package software.bevel.graph_domain.tokenizers

import software.bevel.file_system_domain.LCPosition
import software.bevel.graph_domain.parsing.PotentialSymbol
import java.util.*

class RegexIdentifierTokenizer: IdentifierTokenizer {
    override fun tokenize(wordRegex: Regex, reservedWordsSet: HashSet<String>, relativePath: String, input: String): List<PotentialSymbol> {
        val characters = input.toCharArray()
        val lineStartOffsets = mutableListOf(0)
        var i = 0
        while (i < characters.size) {
            when (characters[i]) {
                '\r' -> {
                    if (i + 1 < characters.size && characters[i + 1] == '\n') {
                        i++ // skip the '\n' part of '\r\n'
                    }
                    lineStartOffsets.add(i + 1)
                }
                '\n' -> {
                    lineStartOffsets.add(i + 1)
                }
            }
            i++
        }

        return wordRegex.findAll(input)
            .mapNotNull { match ->
                val word = match.value.uppercase(Locale.getDefault())
                if (word in reservedWordsSet) return@mapNotNull null
                val start = match.range.first
                val end = match.range.last + 1
                val startPos = findLineColumn(lineStartOffsets, start)
                val endPos = findLineColumn(lineStartOffsets, end)
                PotentialSymbol(match.value, relativePath, startPos, endPos)
            }
            .toList()
    }


    private fun findLineColumn(lineStartOffsets: MutableList<Int>, pos: Int): LCPosition {
        val line = lineStartOffsets.binarySearch { if (it <= pos) -1 else 1 }.let {
            if (it < 0) -(it + 2) else it
        }
        val column = pos - lineStartOffsets.getOrElse(line) { 0 }
        return LCPosition(line, column)
    }
}