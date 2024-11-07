package kr.pandadong2024.babya.server.remote.responses.quiz

import com.google.gson.annotations.SerializedName

data class QuizResponses(
    @field:SerializedName("answer")
    val answer: String? = "Y",
    @field:SerializedName("quizCn")
    val quizCn: String? = "",
    @field:SerializedName("title")
    val title: String? = "",
    @field:SerializedName("quizId")
    val quizId: Int? = -1,
    @field:SerializedName("regDt")
    val regDt: String? = "0000-00-00"
)