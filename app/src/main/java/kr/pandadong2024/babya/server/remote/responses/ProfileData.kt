package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName
import kr.pandadong2024.babya.server.responses.ProfileChildrenData

data class ProfileData (
    @field:SerializedName("nickname")
    val nickname: String? = "",
    @field:SerializedName("dDay")
    val dDay: String? = "0000-00-00",
    @field:SerializedName("birthDt")
    val birthDt: String? = "0000-00-00",
    @field:SerializedName("marriedYears")
    val marriedYears: String? = "0000-00-00",
    @field:SerializedName("children")
    val children: List<ProfileChildrenData>? = listOf(),
    @field:SerializedName("profileImg")
    val profileImg: String? = ""
)