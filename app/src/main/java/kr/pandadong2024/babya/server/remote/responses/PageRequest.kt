package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class PageRequest(
    @field:SerializedName("page")
    val page: Int,
    @field:SerializedName("size")
    val size: Int
)