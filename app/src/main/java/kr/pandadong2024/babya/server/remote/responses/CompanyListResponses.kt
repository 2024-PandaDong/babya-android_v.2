package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class CompanyListResponses(
    @field:SerializedName("companyId")
    val companyId: Int? = 0,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("link")
    val link: String,
    @field:SerializedName("intro")
    val intro: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("state")
    val state: String? = "ACCEPT",
    @field:SerializedName("mtrLvPeriod")
    val mtrLvPeriod: Int? = 0,
    @field:SerializedName("mtrLvSalary")
    val mtrLvSalary: Int? = 0,
    @field:SerializedName("mtrLvIsSalary")
    val mtrLvIsSalary: String? = "Y",
    @field:SerializedName("mtrSupMoney")
    val mtrSupMoney: Int? = 0,
    @field:SerializedName("mtrSupCondition")
    val mtrSupCondition: String,
    @field:SerializedName("telComIsCan")
    val telComIsCan: String? = "Y",
    @field:SerializedName("telComTime")
    val telComTime: Int? = 0,
    @field:SerializedName("telComDays")
    val telComDays: Int? = 0,
    @field:SerializedName("subsdType")
    val subsdType: String,
    @field:SerializedName("subsdMoney")
    val subsdMoney: Int? = 0,
)
