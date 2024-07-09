package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class UserChildren(
    @field:SerializedName("birthName")
    val birthName: String? = "AL-1",
    @field:SerializedName("childId")
    val childId: Int? = -1,
    @field:SerializedName("name")
    val name: String? = "arisu",
    @field:SerializedName("type")
    val type: String? = "null"
)
