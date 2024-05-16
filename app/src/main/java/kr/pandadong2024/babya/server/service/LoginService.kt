package com.babya.server.service

import kr.pandadong2024.babya.server.request.LoginRequest
import kr.pandadong2024.babya.server.responses.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/auth/login")
    suspend fun postLogin(
        @Body body: LoginRequest
    ): LoginResponse
}