package kr.pandadong2024.babya.server.remote.responses

import com.google.gson.annotations.SerializedName

data class CompanyDataResponses(
    @field:SerializedName("companyId")
    val companyId: Int,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("intro")
    val intro: String,
    @field:SerializedName("link")
    val link: String,
    @field:SerializedName("mtrLvIsSalary")
    val mtrLvIsSalary: String,
    @field:SerializedName("mtrLvPeriod")
    val mtrLvPeriod: Int,
    @field:SerializedName("mtrLvSalary")
    val mtrLvSalary: Int,
    @field:SerializedName("mtrSupCondition")
    val mtrSupCondition: String,
    @field:SerializedName("mtrSupMoney")
    val mtrSupMoney: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("state")
    val state: String,
    @field:SerializedName("subsdMoney")
    val subsdMoney: Int,
    @field:SerializedName("subsdType")
    val subsdType: String,
    @field:SerializedName("telComDays")
    val telComDays: Int,
    @field:SerializedName("telComIsCan")
    val telComIsCan: String,
    @field:SerializedName("telComTime")
    val telComTime: Int
)