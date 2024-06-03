package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class BannerImageResponses(
    @field:SerializedName("extension")
    val extension: String, //확장자
    @field:SerializedName("fileId")
    val fileId: Int, //id
    @field:SerializedName("name")
    val name: String, //
    @field:SerializedName("size")
    val size: Int, //
    @field:SerializedName("url")
    val url: String // 이미지 url
)