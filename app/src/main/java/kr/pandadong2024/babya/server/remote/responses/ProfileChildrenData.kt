package kr.pandadong2024.babya.server.responses

import com.google.gson.annotations.SerializedName

data class ProfileChildrenData (
    @field:SerializedName("childId")
    val childId: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("birthName")
    val birthName: String,
    @field:SerializedName("type")
    val type: String
)