package kr.pandadong2024.babya.server.remote.responses.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardCommentResponses(
    @field:SerializedName("commentId")
    val commentId: Int,
    @field:SerializedName("content")
    val content: String,
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("memberId")
    val memberId: String,
    @field:SerializedName("nickname")
    val nickname: String,
    @field:SerializedName("profileImg")
    val profileImg: String,
    @field:SerializedName("subCommentCnt")
    val subCommentCnt: Int
)
