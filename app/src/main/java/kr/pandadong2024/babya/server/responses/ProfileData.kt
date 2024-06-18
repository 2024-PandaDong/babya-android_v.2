package kr.pandadong2024.babya.server.responses

import com.google.gson.annotations.SerializedName

data class ProfileData (
    @field:SerializedName("nickname")
    val nickname: String,
    @field:SerializedName("dDay")
    val dDay: Int,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("marriedYears")
    val marriedYears: Int,
    @field:SerializedName("children")
    val children: List<ProfileChildrenData>,
    @field:SerializedName("profileImg")
    val profileImg: String
)