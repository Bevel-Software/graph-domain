package software.bevel.graph_domain.parsing

import software.bevel.file_system_domain.LCPosition

/**
 * Represents a symbol or identifier found during the parsing process that might be relevant for graph construction.
 * It captures the name of the symbol and its precise location (file path, start and end positions) in the source code.
 *
 * @property name The name of the identified symbol (e.g., variable name, function name, class name).
 * @property filePath The absolute path to the file where this symbol is located.
 * @property startPosition The starting position (line and column) of the symbol in the source file.
 * @property endPosition The ending position (line and column) of the symbol in the source file.
 */
data class PotentialSymbol(
    val name: String,
    val filePath: String,
    val startPosition: LCPosition,
    val endPosition: LCPosition
)