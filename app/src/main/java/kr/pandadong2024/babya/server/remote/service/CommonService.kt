package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CommonService {
    @Multipart
    @POST("/upload")
    suspend fun fileUpload(
        @Header("Authorization") accessToken: String,
        @Part file: MultipartBody.Part
    ): BaseResponse<String>
}