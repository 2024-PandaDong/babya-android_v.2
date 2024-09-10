package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.CompanyListResponses
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CompanyService {

    @GET("/company")
    suspend fun getCompanyList(
        @Header("Authorization")accessToken : String,
        @Query("page") page : Int,
        @Query("size") size : Int
    ):BaseResponse<List<CompanyListResponses>>
}