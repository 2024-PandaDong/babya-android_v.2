package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class EmailResponse(
    @field:SerializedName("status")
    val status: Int,
    @field:SerializedName("message")
    val message: String
)
