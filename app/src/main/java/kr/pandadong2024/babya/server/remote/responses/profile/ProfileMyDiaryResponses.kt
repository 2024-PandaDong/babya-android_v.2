package kr.pandadong2024.babya.server.remote.responses.profile

import com.google.gson.annotations.SerializedName

data class ProfileMyDiaryResponses(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("emoji")
    val emoji: String,
    @field:SerializedName("writtenDt")
    val writtenDt: String
)
