package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class DiaryFile(
    @field:SerializedName("extension")
    val extension: String = "test",
    @field:SerializedName("fileId")
    val fileId: Int = 1,
    @field:SerializedName("name")
    val name: String = "test",
    @field:SerializedName("size")
    val size: Int = 1,
    @field:SerializedName("url")
    val url: String = "test"
)