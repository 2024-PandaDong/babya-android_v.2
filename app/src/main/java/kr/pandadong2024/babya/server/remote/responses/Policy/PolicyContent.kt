package kr.pandadong2024.babya.server.remote.responses.Policy

import com.google.gson.annotations.SerializedName

data class PolicyContent(
    @field:SerializedName("content")
    val content: String,
    @field:SerializedName("editDate")
    val editDate: String,
    @field:SerializedName("link")
    val link: String,
    @field:SerializedName("title")
    val title: String
)