package kr.pandadong2024.babya.server.remote.responses.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardDataResponses(
    @field:SerializedName("postId")
    val postId: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("content")
    val content: String,
    @field:SerializedName("category")
    val category: String,
    @field:SerializedName("view")
    val view: Int,
    @field:SerializedName("commentCnt")
    val commentCnt: Int,
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("files")
    val files: List<DashBoardFileResponses>,
    @field:SerializedName("memberId")
    val memberId: String,
    @field:SerializedName("profileImg")
    val profileImg: String,
    @field:SerializedName("nickname")
    val nickname: String,
)
