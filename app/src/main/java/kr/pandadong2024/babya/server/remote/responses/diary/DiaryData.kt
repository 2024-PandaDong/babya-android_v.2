package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class DiaryData(
    @field:SerializedName("content")
    val content: String = "test",
    @field:SerializedName("diaryId")
    val diaryId: Int = 1,
    @field:SerializedName("diaryState")
    val diaryState: String = "test",
    @field:SerializedName("diastolicPressure")
    val diastolicPressure: Int = 1,
    @field:SerializedName("emojiCode")
    val emojiCode: String = "test",
    @field:SerializedName("fetusComment")
    val fetusComment: String = "test",
    @field:SerializedName("isPublic")
    val isPublic: String = "test",
    @field:SerializedName("member")
    val member: Member = Member(),
    @field:SerializedName("memo")
    val memo: String = "test",
    @field:SerializedName("nextAppointment")
    val nextAppointment: String = "test",
    @field:SerializedName("pregnancyWeeks")
    val pregnancyWeeks: Int = 1,
    @field:SerializedName("systolicPressure")
    val systolicPressure: Int = 1,
    @field:SerializedName("title")
    val title: String = "test",
    @field:SerializedName("weight")
    val weight: Int = 1,
    @field:SerializedName("writtenDt")
    val writtenDt: String = "test"
)