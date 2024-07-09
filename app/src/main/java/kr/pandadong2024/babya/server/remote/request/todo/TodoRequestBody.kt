package kr.pandadong2024.babya.server.remote.request.todo

import com.google.gson.annotations.SerializedName

data class TodoRequestBody(
    @field:SerializedName("category")
    val category: String? = "null",
    @field:SerializedName("content")
    val content: String? = "null",
    @field:SerializedName("planedDt")
    val planedDt: String? = "2014-09-04"
)
