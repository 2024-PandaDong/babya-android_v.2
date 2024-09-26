package kr.pandadong2024.babya.server.remote.responses.Policy

import com.google.gson.annotations.SerializedName

data class PolicyListResponse(
    @field:SerializedName("editDate")
    val editDate: String? = "",
    @field:SerializedName("policyId")
    val policyId: Int? = 0,
    @field:SerializedName("title")
    val title: String? = "데이터가 없습니다."
)