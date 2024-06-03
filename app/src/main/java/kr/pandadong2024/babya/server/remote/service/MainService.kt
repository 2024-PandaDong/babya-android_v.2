package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BannerResponses
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import retrofit2.http.GET
import retrofit2.http.HEAD
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
}

//@GET("/article/{id}")
//        fun checkBoard(
//            @Path(value = "id") id:String
//        ):Call<GuidedResponse<CheckBoardResponse>>