package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @field:SerializedName("status")
    val status: Int? = -1,
    @field:SerializedName("message")
    val message: String? = "그냥 오류 이건 내부에서 오류난거임",
    @field:SerializedName("data")
    val data: T? = null,
)