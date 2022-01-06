package data.second.model

import com.google.gson.annotations.SerializedName

data class KeyWord(
    @SerializedName("keyWord")
    val keyWord: String,
    @SerializedName("proposals")
    val proposals: List<SecondProposals>,
)
