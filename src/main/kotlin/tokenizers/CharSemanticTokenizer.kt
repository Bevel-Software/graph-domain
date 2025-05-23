package software.bevel.graph_domain.tokenizers

class CharSemanticTokenizer: SemanticTokenizer {

    override fun tokenizeCode(input: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        val charArray = input.toCharArray()

        var i = 0
        while (i < charArray.size) {
            when (val c = charArray[i]) {
                in 'a'..'z', in 'A'..'Z', in '0'..'9', '_' -> {
                    current.append(c)
                }
                ' ', '\t', '\n', '\r' -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current.clear()
                    }
                }
                else -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current.clear()
                    }
                    if (i + 1 < charArray.size) {
                        when (val twoCharOp = "$c${charArray[i + 1]}") {
                            "==", "!=", "<=", ">=", "&&", "||" -> {
                                tokens.add(twoCharOp)
                                i++
                            }
                            else -> {
                                tokens.add(c.toString())
                            }
                        }
                    } else {
                        tokens.add(c.toString())
                    }
                }
            }
            i++
        }

        if (current.isNotEmpty()) {
            tokens.add(current.toString())
        }

        return tokens
    }
}