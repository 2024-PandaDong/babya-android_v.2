package kr.pandadong2024.babya.server.remote.responses.todo

import com.google.gson.annotations.SerializedName

data class TodoResponses(
    @field:SerializedName("todoId")
    val todoId : Int? = -1,
    @field:SerializedName("content")
    val content : String? = "null",
    @field:SerializedName("planedDt")
    val planedDt : String? = "0000-00-00",
    @field:SerializedName("isChecked")
    var isChecked : Boolean? = false,
    @field:SerializedName("category")
    val category : String? = "null",
)