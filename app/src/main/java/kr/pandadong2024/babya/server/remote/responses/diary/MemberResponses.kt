package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class MemberResponses(
    @field:SerializedName("answerDt")
    val answerDt: String = "test",
    @field:SerializedName("birthDt")
    val birthDt: String = "test",
    @field:SerializedName("childCnt")
    val childCnt: Int = 1,
    @field:SerializedName("createdAt")
    val createdAt: String = "test",
    @field:SerializedName("email")
    val email: String = "test",
    @field:SerializedName("lc")
    val lc: String = "test",
    @field:SerializedName("marriedDt")
    val marriedDt: String = "test",
    @field:SerializedName("memberState")
    val memberState: String = "test",
    @field:SerializedName("nickName")
    val nickName: String = "test",
    @field:SerializedName("pregnancyDt")
    val pregnancyDt: String = "test",
    @field:SerializedName("recentConDt")
    val recentConDt: String = "test"
)