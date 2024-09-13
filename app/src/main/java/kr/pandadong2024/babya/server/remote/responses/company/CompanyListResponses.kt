package kr.pandadong2024.babya.server.remote.responses.company

import com.google.gson.annotations.SerializedName

data class CompanyListResponses(
    @field:SerializedName("companyId")
    val companyId: Int? = 0,
    @field:SerializedName("companyName")
    val companyName: String? = "-1",
    @field:SerializedName("address")
    val address: String? = "-1",
    @field:SerializedName("logoImg")
    val logoImg: List<String>? = listOf("-1")
)