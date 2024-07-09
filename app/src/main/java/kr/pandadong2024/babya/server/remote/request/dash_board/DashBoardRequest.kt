package kr.pandadong2024.babya.server.remote.request.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardRequest(
    @field:SerializedName("title")
    val title : String,
    @field:SerializedName("content")
    val content : String,
    @field:SerializedName("category")
    val category : String,
    @field:SerializedName("file")
    val file : List<String>
)
