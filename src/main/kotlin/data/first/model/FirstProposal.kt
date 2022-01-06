package data.first.model

import com.google.gson.annotations.SerializedName
import data.common.model.Competence
import java.util.*

data class FirstProposal(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("beginDate")
    val beginDate: Date,
    @SerializedName("endDate")
    val endDate: Date,
    @SerializedName("value")
    val value: Float,
    @SerializedName("isGeneric")
    val isGeneric: Boolean,
    @SerializedName("isGoods")
    val isGoods: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("competence")
    val competence: Competence,
    @SerializedName("keyWord")
    val keyWord: List<String>,
)