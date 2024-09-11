package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyContent
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PolicyService {
    @GET("/policy")
    suspend fun getPolicyList(
        @Query("region") type: String
    ): BaseResponse<List<PolicyListResponse>>

    @GET("/policy/{id}")
    suspend fun getPolicyContent(
        @Path("id") id: Int,
    ): BaseResponse<PolicyContent>
}
