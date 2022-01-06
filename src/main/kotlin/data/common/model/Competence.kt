package data.common.model

import com.google.gson.annotations.SerializedName

data class Competence(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
)
