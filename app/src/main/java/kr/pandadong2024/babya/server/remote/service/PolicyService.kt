package kr.pandadong2024.babya.server.remote.service

import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.Policy.PolicyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PolicyService {
    @GET("/policy")
    suspend fun getPolicy(
        @Query("region") type : String
    ): BaseResponse<List<PolicyResponse>>
}