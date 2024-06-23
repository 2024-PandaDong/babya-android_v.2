package kr.pandadong2024.babya.server.remote.request

import com.google.gson.annotations.SerializedName

data class SubCommentRequest(
    @field:SerializedName("comment")
    val comment: String? = "null",
    @field:SerializedName("diaryId")
    val diaryId: Int? = -1,
    @field:SerializedName("parentCommentId")
    val parentCommentId: Int? = null
)