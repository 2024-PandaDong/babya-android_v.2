package kr.pandadong2024.babya.server.service

import kr.pandadong2024.babya.server.request.SignUpRequest
import kr.pandadong2024.babya.server.responses.EmailResponse
import kr.pandadong2024.babya.server.responses.SignupResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SignupService {
    @POST("/auth/join")
    suspend fun postSignup(
        @Body body: SignUpRequest
    ): SignupResponse

    @POST("/auth/email-send")
    suspend fun postEmailSend(
        @Query("email") email : String
    ): EmailResponse

    @POST("/auth/email-verify")
    suspend fun postEmailVerify(
        @Query("email") email: String,
        @Query("code") code : String
    ): EmailResponse
}