package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.UserDataResponses
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CommonService {
    @Multipart
    @POST("/upload")
    suspend fun fileUpload(
        @Header("Authorization") accessToken: String,
        @Part file: MultipartBody.Part
    ): BaseResponse<String>

    @GET("/member/profile/{email}")
    suspend fun getProfile(
        @Header("Authorization") accessToken: String,
        @Path("email") email : String
    ) : BaseResponse<UserDataResponses>
}