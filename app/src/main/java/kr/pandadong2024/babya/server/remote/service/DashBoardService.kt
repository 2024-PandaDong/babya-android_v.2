package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.request.dash_board.DashBoardCommentRequest
import kr.pandadong2024.babya.server.remote.request.dash_board.DashBoardRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardCommentResponses
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardDataResponses
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardResponses
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DashBoardService {
    @GET("/post/list")
    suspend fun getDashBoardList(
        @Header("Authorization")accessToken : String,
        @Query("page")page: Int,
        @Query("size")size: Int,
        @Query("category")category : String
    ): BaseResponse<List<DashBoardResponses>>

    @POST("/post")
    suspend fun postDashBoard(
        @Header("Authorization")accessToken : String,
        @Body body:DashBoardRequest
    ) : BaseResponse<Unit>

    @GET("/post/{id}")
    suspend fun getDashBoard(
        @Header("Authorization")accessToken : String,
        @Query("id")id: Int
    ):BaseResponse<DashBoardDataResponses>

    @GET("/post/comment")
    suspend fun getComment(
        @Header("Authorization")accessToken : String,
        @Query("page")page: Int,
        @Query("size")size: Int,
        @Query("postId")postId: Int
    ):BaseResponse<List<DashBoardCommentResponses>>

    @POST("/post/comment")
    suspend fun postComment(
        @Header("Authorization")accessToken : String,
        @Body body: DashBoardCommentRequest
    ):BaseResponse<Unit>
}