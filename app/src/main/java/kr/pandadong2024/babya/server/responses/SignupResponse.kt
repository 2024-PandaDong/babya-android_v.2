package kr.pandadong2024.babya.server.responses

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @field:SerializedName("status")
    val status: Int,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: TokenDataResponses,
)
