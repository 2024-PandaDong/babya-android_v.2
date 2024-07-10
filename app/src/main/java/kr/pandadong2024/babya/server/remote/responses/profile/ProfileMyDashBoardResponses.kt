package kr.pandadong2024.babya.server.remote.responses.profile

import com.google.gson.annotations.SerializedName

data class ProfileMyDashBoardResponses(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("createdAt")
    val createdAt: String
)
