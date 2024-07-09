package kr.pandadong2024.babya.server.remote.request.diary

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class PostDiaryRequest(
    @field:SerializedName("title")
    val title: String? = "null",
    @field:SerializedName("content")
    val content: String? = "null",
    @field:SerializedName("pregnancyWeeks")
    val pregnancyWeeks: Int? = -1,
    @field:SerializedName("weight")
    val weight: Int? = -1,
    @field:SerializedName("systolicPressure")
    val systolicPressure: Int? = -1,
    @field:SerializedName("diastolicPressure")
    val diastolicPressure: Int? = -1,
    @field:SerializedName("nextAppointment")
    val nextAppointment: String? = "2024-12-12",
    @field:SerializedName("emoji")
    val emoji: String? = "null",
    @field:SerializedName("fetusComment")
    val fetusComment: String? = "null",
    @field:SerializedName("isPublic")
    val isPublic: Boolean? = false,
    @field:SerializedName("url")
    val url: List<String>? = listOf(),
)