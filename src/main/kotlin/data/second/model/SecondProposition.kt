package data.second.model

import data.common.model.Competence
import java.util.*

data class SecondProposition(
    val id: Int,
    val userId: Int,
    val beginDate: Date,
    val endDate: Date,
    val value: Float,
    val isGeneric: Boolean,
    val isGoods: Boolean,
    val name: String,
    val description: String,
    val competence: Competence,
)