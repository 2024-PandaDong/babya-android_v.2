package kr.pandadong2024.babya.server.service

import kr.pandadong2024.babya.server.remote.request.UserEditRequest
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDashBoardResponses
import kr.pandadong2024.babya.server.remote.responses.profile.ProfileMyDiaryResponses
import kr.pandadong2024.babya.server.remote.responses.ProfileData
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query


interface ProfileService {
    @GET("/member/profile/{email}")
    suspend fun getProfile(
        @Header("Authorization")accessToken : String,
        @Path("email") email : String
    ): BaseResponse<ProfileData>


    @PATCH("/member/profile")
    suspend fun editUserInfo(
        @Header("Authorization")accessToken : String,
        @Body body : UserEditRequest
    ):BaseResponse<Unit>

    @PATCH("/member")
    suspend fun editUserImage(
        @Header("Authorization")accessToken : String,
        @Query("imgUrl") imgUrl : String
    ):BaseResponse<Unit>

    @GET("/post/my")
    suspend fun getMyDashBoard(
        @Header("Authorization")accessToken : String,
        @Query("page")page: Int,
        @Query("size")size: Int
    ):BaseResponse<List<ProfileMyDashBoardResponses>>

    @GET("/diary/my/profile")
    suspend fun getMyDiary(
        @Header("Authorization")accessToken : String,
        @Query("page")page: Int,
        @Query("size")size: Int
    ):BaseResponse<List<ProfileMyDiaryResponses>>

    @DELETE("/member/withdraw")
    suspend fun deleteMember(
        @Header("Authorization")accessToken : String
    ):BaseResponse<Unit>

    @GET("/member/lc")
    suspend fun getLocalCode(
        @Header("Authorization")accessToken : String
    ):BaseResponse<String>
}