package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class CommentResponses(
    @field:SerializedName("commentId")
    val commentId: Int? = -1,
    @field:SerializedName("content")
    val content: String? = "null",
    @field:SerializedName("createdAt")
    val createdAt: String? = "null",
    @field:SerializedName("memberId")
    val memberId: String? = "null",
    @field:SerializedName("nickname")
    val nickname: String? = "null",
    @field:SerializedName("profileImg")
    val profileImg: String? = "https://i.namu.wiki/i/yHVl4zBU80OEszMyE1k074jAh6UOcn2TFMZwnh4qNAPZqZDGcowAQeD8vvpQSfTOP6OsjWuENU9Iy2A1Cg_7Sg.webp",
    @field:SerializedName("subCommentCnt")
    val subCommentCnt: Int? = -1
)