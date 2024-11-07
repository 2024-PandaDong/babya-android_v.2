package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.SubCommentRequest
import kr.pandadong2024.babya.server.remote.request.diary.EditDiaryRequest
import kr.pandadong2024.babya.server.remote.request.diary.PostDiaryRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.CommentResponses
import kr.pandadong2024.babya.server.remote.responses.SubCommentResponses
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {

    @GET("/diary/my")
    suspend fun getMyDiaryList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = "",
    ): BaseResponse<List<DiaryDataResponses>>

    @GET("/diary/list") // 1부터 있음
    suspend fun getDiaryList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = "",
    ): BaseResponse<List<DiaryDataResponses>>

    @POST("/diary")
    suspend fun postDiary(
        @Header("Authorization") accessToken: String,
        @Body request: PostDiaryRequest,
    ): BaseResponse<String>

    @GET("/diary/{id}")
    suspend fun getDiaryData(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): BaseResponse<DiaryDataResponses>

    @PATCH("/diary/{id}")
    suspend fun modifyDiary(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int,
        @Body body: EditDiaryRequest
    ): BaseResponse<String>

    @PATCH("/diary/report/{id}")
    suspend fun reportDiary(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int,
    ): BaseResponse<String>

    @DELETE("/diary/{id}")
    suspend fun deleteDiary(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): BaseResponse<String>

    @GET("/diary/comment")
    suspend fun getComment(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("diaryId") diaryId: Int
    ): BaseResponse<List<CommentResponses>>

    @GET("/diary/sub-comment")
    suspend fun getSubComment(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("parentId") parentId: Int
    ): BaseResponse<List<SubCommentResponses>>

    @POST("/diary/comment")
    suspend fun postComment(
        @Header("Authorization") accessToken: String,
        @Body body: SubCommentRequest
    ): BaseResponse<String>

}