package kr.pandadong2024.babya.server.remote.request

import com.google.gson.annotations.SerializedName

data class UserEditRequest(
    @field:SerializedName("birthDt")
    var birthDt: String? = null,
    @field:SerializedName("lc")
    var lc: String? = null,
    @field:SerializedName("marriedDt")
    var marriedDt: String? = null,
    @field:SerializedName("nickName")
    var nickName: String? = null,
    @field:SerializedName("pregnancyDt")
    var pregnancyDt: String? = null
)