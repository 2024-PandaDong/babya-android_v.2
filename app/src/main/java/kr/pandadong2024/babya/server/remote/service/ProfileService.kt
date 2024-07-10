package kr.pandadong2024.babya.server.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDashBoardResponses
import kr.pandadong2024.babya.server.responses.ProfileData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface ProfileService {
    @GET("/member/profile/{email}")
    suspend fun getProfile(
        @Header("Authorization")accessToken : String,
        @Path("email") email : String
    ): BaseResponse<ProfileData>

    @GET("/post/my")
    suspend fun getMyDashBoard(
        @Header("Authorization")accessToken : String,
        @Query("page")page: Int,
        @Query("size")size: Int
    ):BaseResponse<List<ProfileMyDashBoardResponses>>
}