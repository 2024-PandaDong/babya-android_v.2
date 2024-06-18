package kr.pandadong2024.babya.server.remote.request

import kr.pandadong2024.babya.start.signup.BirthName
import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @field:SerializedName("email")
    val email:String,
    @field:SerializedName("pw")
    val pw:String,
    @field:SerializedName("nickName")
    val nickName:String,
    @field:SerializedName("marriedDt")
    val marriedDt:String,
    @field:SerializedName("pregnancyDt")
    val pregnancyDt:String,
    @field:SerializedName("birthDt")
    val birthDt:String,
    @field:SerializedName("locationCode")
    val locationCode:String,
    @field:SerializedName("pushToken")
    val pushToken:String,
    @field:SerializedName("childList")
    val childList:List<BirthName>,

    )
