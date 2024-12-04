package kr.pandadong2024.babya.server.kakao.remote.service

import retrofit2.Response
import kr.pandadong2024.babya.server.kakao.remote.responses.KakaoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApiService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Header("Authorization") authorization: String,
        @Query("y") latitude: Double,
        @Query("x") longitude: Double,
        @Query("radius") radius: Int,
        @Query("query") query: String
    ): Response<KakaoResponse>
}