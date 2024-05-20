package kr.pandadong2024.babya.server.responses

import com.google.gson.annotations.SerializedName

data class BannerResponses(
    @field:SerializedName("content")
    val content: String, //TODO 링크 없을때 업는거 사진넣기
    @field:SerializedName("regDt")
    val regDt: String,
    @field:SerializedName("source")
    val source: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("url")
    val url: String
)