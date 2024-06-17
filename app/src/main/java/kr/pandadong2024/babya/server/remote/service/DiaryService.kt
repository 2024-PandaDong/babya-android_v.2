package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.PageRequest
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiaryService {

    @GET("/diary/my")
    suspend fun getMyDiaryData(
        @Header("Authorization")accessToken : String,
        @Query("pageRequest") pageRequest : PageRequest,
    ):BaseResponse<List<DiaryDataResponses>>

    @GET("/diary/list") // 1부터 있음
    suspend fun getOtherDiaryList(
        @Header("Authorization")accessToken : String,
        @Query("pageRequest")pageRequest:PageRequest,
    ):BaseResponse<List<DiaryDataResponses>>

//    @GET("/diary/comment")
//    suspend fun getComment(): BaseResponse<>
//
//    @GET("/diary/comment")
//    suspend fun getComment(): BaseResponse<>
//
//    @GET("/diary/comment")
//    suspend fun getComment(): BaseResponse<>
//
//    @GET("/diary/comment")
//    suspend fun getComment(): BaseResponse<>


}