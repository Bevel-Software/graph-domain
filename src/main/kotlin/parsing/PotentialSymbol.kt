package software.bevel.graph_domain.parsing

import software.bevel.file_system_domain.LCPosition

data class PotentialSymbol(
    val name: String,
    val filePath: String,
    val startPosition: LCPosition,
    val endPosition: LCPosition
)