package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.CompanyDataResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MainService {
    @GET("/banner/{lc}")
    suspend fun getBanner(
        @Header("Authorization")accessToken : String,
        @Path(value = "lc") lc : String,
        @Query("type") type : String
    ): BaseResponse<List<BannerResponses>>

    @GET("/company")
    suspend fun getCompanyData(
        @Query("page") page : Int,
        @Query("size") size : Int
    ):BaseResponse<List<CompanyDataResponses>>

    @GET("/diary/my")
    suspend fun getMyDiaryData(
        @Query("page") page : Int,
        @Query("size") size : Int
    ):BaseResponse<List<DiaryData>>

    @GET("/diary")
    suspend fun getAllDiaryData(
        @Query("page") page : Int,
        @Query("size") size : Int
    ):BaseResponse<List<DiaryData>>
}

