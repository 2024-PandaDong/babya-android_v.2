package kr.pandadong2024.babya.server.remote.responses.company

import com.google.gson.annotations.SerializedName

data class CompanyListResponses(
    @field:SerializedName("companyId")
    val companyId: Int? = 0,
    @field:SerializedName("name")
    val companyName: String? = "-1",
    @field:SerializedName("link")
    val address: String? = "-1",
    @field:SerializedName("intro")
    val logoImg: String? = "-1"
)
