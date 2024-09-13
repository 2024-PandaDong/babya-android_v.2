package kr.pandadong2024.babya.server.remote.responses.Policy

import com.google.gson.annotations.SerializedName

data class PolicyListResponse(
    @field:SerializedName("editDate")
    val editDate: String? = "2014-12-12",
    @field:SerializedName("policyId")
    val policyId: Int? = -1,
    @field:SerializedName("title")
    val title: String? = "없음"
)