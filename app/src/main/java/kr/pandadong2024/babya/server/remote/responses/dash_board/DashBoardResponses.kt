package kr.pandadong2024.babya.server.remote.responses.dash_board

import com.google.gson.annotations.SerializedName

data class DashBoardResponses(
    @field:SerializedName("postId")
    val postId: Int? = -1,
    @field:SerializedName("title")
    val title: String? = "동바오가 위험해요",
    @field:SerializedName("content")
    val content: String? = "도와주세요",
    @field:SerializedName("category")
    val category: String? = "1",
    @field:SerializedName("view")
    val view: Int? = 40,
    @field:SerializedName("commentCnt")
    val commentCnt: Int? = -1,
    @field:SerializedName("createdAt")
    val createdAt: String? = "2024-06-25T06:45:26.269Z",
    @field:SerializedName("memberId")
    val memberId: String? = "",
    @field:SerializedName("nickname")
    val nickname: String? = "동찬이 맘"
)
