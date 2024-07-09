package kr.pandadong2024.babya.server.remote.responses.diary

import com.google.gson.annotations.SerializedName

data class DiaryDataResponses(
    @field:SerializedName("content")
    val content: String? = "test",
    @field:SerializedName("diaryId")
    val diaryId: Int? = -1,
    @field:SerializedName("diastolicPressure")
    val diastolicPressure: Int? = -1,
    @field:SerializedName("emojiCode")
    val emojiCode: String? = "",
    @field:SerializedName("fetusComment")
    val fetusComment: String? = "test",
    @field:SerializedName("files")
    val files: List<DiaryFileResponses>? = listOf(DiaryFileResponses()),
    @field:SerializedName("isPublic")
    val isPublic: String? = "true",
    @field:SerializedName("memberId")
    val memberId: String? = "null",
    @field:SerializedName("memo")
    val memo: String? = "test",
    @field:SerializedName("nextAppointment")
    val nextAppointment: String? = "12/12",
    @field:SerializedName("nickname")
    val nickname: String? = "tester",
    @field:SerializedName("pregnancyWeeks")
    val pregnancyWeeks: Int? = -1,
    @field:SerializedName("systolicPressure")
    val systolicPressure: Int? = -1,
    @field:SerializedName("title")
    val title: String? = "test",
    @field:SerializedName("weight")
    val weight: Int? = -1,
    @field:SerializedName("writtenDt")
    val writtenDt: String? = "0000-00-00"
)