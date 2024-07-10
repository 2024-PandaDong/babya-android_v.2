package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.quiz.QuizResponses
import retrofit2.http.GET
import retrofit2.http.Header

interface QuizService {
    @GET("/quiz")
    suspend fun getQuiz(
        @Header("Authorization")accessToken : String
    ):BaseResponse<QuizResponses>
}