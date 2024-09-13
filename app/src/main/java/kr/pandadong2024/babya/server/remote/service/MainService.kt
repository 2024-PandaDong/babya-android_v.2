package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.main.UserWeekStatus
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MainService {
    @GET("/banner/{lc}")
    suspend fun getBanner(
        @Header("Authorization")accessToken : String,
        @Path("lc") lc : String,
        @Query("type") type : String
    ): BaseResponse<List<BannerResponses>>

    @GET("/company")
    suspend fun getCompanyData(
        @Header("Authorization")accessToken : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ):BaseResponse<List<kr.pandadong2024.babya.server.remote.responses.company.CompanyDataResponses>>
    @GET("/diary/staus/week")
    suspend fun getUserWeekStatus(
        @Header("Authorization")accessToken : String
    ):BaseResponse<UserWeekStatus>


}

