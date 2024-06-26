package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @field:SerializedName("status")
    val status: Int? = -1,
    @field:SerializedName("message")
    val message: String? = "초기화 안됨",
    @field:SerializedName("data")
    val data: T? = null,
)