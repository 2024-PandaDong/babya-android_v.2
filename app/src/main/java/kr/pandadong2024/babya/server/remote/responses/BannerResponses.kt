package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class BannerResponses(

    @field:SerializedName("url")
    val url: String,// 웹 사이트
    @field:SerializedName("subTitle")
    val subTitle: String, // 정책 이름
    @field:SerializedName("source")
    val source: String, // 출처
    @field:SerializedName("image")
    val image: BannerImageResponses

)
