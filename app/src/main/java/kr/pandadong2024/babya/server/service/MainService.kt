package kr.pandadong2024.babya.server.service

import kr.pandadong2024.babya.server.responses.BannerResponses
import kr.pandadong2024.babya.server.responses.BaseResponse
import retrofit2.http.GET

interface MainService {
    @GET("/banner")
    suspend fun getBanner(): BaseResponse<List<BannerResponses>>
}