package com.babya.server.service

import kr.pandadong2024.babya.server.remote.request.LoginRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.TokenDataResponses
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/auth/login")
    suspend fun postLogin(
        @Body body: LoginRequest
    ): BaseResponse<TokenDataResponses>
}