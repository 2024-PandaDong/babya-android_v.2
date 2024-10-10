package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.LoginRequest
import kr.pandadong2024.babya.server.remote.request.RefreshRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.TokenDataResponses
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/auth/login")
    suspend fun postLogin(
        @Body body: LoginRequest
    ): BaseResponse<TokenDataResponses>

    @POST("/auth/reissue")
    suspend fun requestRefresh(
        @Body refreshToken : RefreshRequest
    ) : BaseResponse<String>

}