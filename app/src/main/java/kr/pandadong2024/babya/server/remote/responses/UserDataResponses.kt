package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class UserDataResponses(
    @field:SerializedName("age")
    val age: Int? = -1,
    @field:SerializedName("children")
    val children: List<UserChildren> = listOf<UserChildren>(),
    @field:SerializedName("dDay")
    val dDay: String? = "",
    @field:SerializedName("marriedYears")
    val marriedYears: String?=  "",
    @field:SerializedName("nickname")
    val nickname: String? = "null",
    @field:SerializedName("profileImg")
    val profileImg: String? = "https://i.namu.wiki/i/yHVl4zBU80OEszMyE1k074jAh6UOcn2TFMZwnh4qNAPZqZDGcowAQeD8vvpQSfTOP6OsjWuENU9Iy2A1Cg_7Sg.webp",
)
