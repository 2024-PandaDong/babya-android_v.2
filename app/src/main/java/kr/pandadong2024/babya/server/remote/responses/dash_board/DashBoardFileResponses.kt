package kr.pandadong2024.babya.server.remote.responses.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardFileResponses(
    @field:SerializedName("fileId")
    val fileId: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("size")
    val size: Int,
    @field:SerializedName("extension")
    val extension: String,
    @field:SerializedName("url")
    val url: String
)
