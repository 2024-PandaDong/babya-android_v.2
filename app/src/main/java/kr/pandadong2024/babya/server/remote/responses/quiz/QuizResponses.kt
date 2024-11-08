package kr.pandadong2024.babya.server.remote.responses.quiz

import com.google.gson.annotations.SerializedName

data class QuizResponses(
    @field:SerializedName("answer")
    val answer: String? = "Y",
    @field:SerializedName("quizCn")
    val quizCn: String? = "없음?",
    @field:SerializedName("title")
    val title: String? = "없음?",
    @field:SerializedName("quizId")
    val quizId: Int? = -1,
    @field:SerializedName("regDt")
    val regDt: String? = "0000-00-00"
)