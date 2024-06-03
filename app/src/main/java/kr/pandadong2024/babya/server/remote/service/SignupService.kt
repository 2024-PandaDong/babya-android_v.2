package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.SignUpRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.TokenDataResponses
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SignupService {
    @POST("/auth/join")
    suspend fun postSignup(
        @Body body: SignUpRequest
    ): BaseResponse<TokenDataResponses>

    @POST("/auth/email-send")
    suspend fun postEmailSend(
        @Query("email") email : String
    ): BaseResponse<Unit>

    @POST("/auth/email-verify")
    suspend fun postEmailVerify(
        @Query("email") email: String,
        @Query("code") code : String
    ): BaseResponse<Unit>
}