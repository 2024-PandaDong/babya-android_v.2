package kr.pandadong2024.babya.server.service

import kr.pandadong2024.babya.server.responses.BaseResponse
import kr.pandadong2024.babya.server.responses.ProfileData
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {
    @GET("/number/profile/{email}")
    suspend fun getProfile(
        @Path("email") email : String
    ):BaseResponse<ProfileData>
}