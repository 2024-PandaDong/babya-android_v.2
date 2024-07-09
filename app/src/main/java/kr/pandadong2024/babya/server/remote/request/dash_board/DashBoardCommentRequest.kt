package kr.pandadong2024.babya.server.remote.request.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardCommentRequest(
    @field:SerializedName("comment")
    val comment: String,
    @field:SerializedName("postId")
    val postId: Int,
    @field:SerializedName("parentCommentId")
    val parentCommentId: Int?
)
