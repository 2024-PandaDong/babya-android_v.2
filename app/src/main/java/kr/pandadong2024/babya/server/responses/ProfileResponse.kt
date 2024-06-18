package kr.pandadong2024.babya.server.responses

import com.google.gson.annotations.SerializedName
import kr.pandadong2024.babya.signup.BirthName

data class ProfileResponse<T> (
    @field:SerializedName("status")
    val status: Int,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: T,
)
