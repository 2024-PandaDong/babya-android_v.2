package kr.pandadong2024.babya.server.remote.responses.Policy

import com.google.gson.annotations.SerializedName

data class PolicyResponse(
    @field:SerializedName("editDate")
    val editDate: String,
    @field:SerializedName("policyId")
    val policyId: Int,
    @field:SerializedName("title")
    val title: String
)