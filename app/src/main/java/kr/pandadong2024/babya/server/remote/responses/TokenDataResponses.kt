package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class TokenDataResponses(
    @field:SerializedName("accessToken")
    val accessToken: String,
    @field:SerializedName("refreshToken")
    val refreshToken: String,
)