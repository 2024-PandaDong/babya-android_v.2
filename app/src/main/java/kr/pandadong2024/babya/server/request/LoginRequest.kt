package kr.pandadong2024.babya.server.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("pw")
    val pw: String
)
