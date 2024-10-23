package kr.pandadong2024.babya.server.remote.responses.company

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CompanyResponses(
    @field:SerializedName("name")  // 회사명
    val name: String? = "",
    @field:SerializedName("link") // 화사링크
    val link: String? ="",
    @field:SerializedName("description") // 설명
    val description: String? = "",
    // 헤택 및 복지
    @field:SerializedName("mtrLvPeriod") // 육아 휴직 기간
    val mtrLvPeriod: Int? = -1,
    @field:SerializedName("mtrLvSalary") // 육아 휴직 급여 비율
    val mtrLvSalary: Int? = -1,
    @field:SerializedName("mtrLvIsSalary") // 유급 여부
    val mtrLvIsSalary: String? = "",
    @field:SerializedName("mtrSupMoney") // 출산 비용 지원금
    val mtrSupMoney: Int? = -1,
    @field:SerializedName("mtrSupCondition") // 출산 비용 지원 조건
    val mtrSupCondition: String? = "",
    @field:SerializedName("telComDays") // 재택근무 일 수
    val telComDays: Int? = -1,
    @field:SerializedName("telComIsCan") // 재택근무 가능 여부
    val telComIsCan: String? = "",
    @field:SerializedName("telComTime") // 재택근무 시간
    val telComTime: Int? = -1,
    @field:SerializedName("subsdType") // 보조금 지원 종류
    val subsdType: String? = "",
    @field:SerializedName("subsdMoney") // 보조금 지원 액
    val subsdMoney: Int? = -1,

    // 회사 설명
    @field:SerializedName("minSalary") // 최소 연봉
    val minSalary: Int? = -1,
    @field:SerializedName("maxSalary") // 최대 연봉
    val maxSalary: Int? = -1,
    @field:SerializedName("avgSalary") // 평균 연봉
    val avgSalary: Int? = -1,
    @field:SerializedName("salaryYear") // 기준 연도
    val salaryYear: Int? = -1,
    @field:SerializedName("femalePeople") // 여성 근무자 수
    val femalePeople: Int? = 0,
    @field:SerializedName("malePeople") // 남성 근무자 수
    val malePeople: Int? = 0,
    @field:SerializedName("ceo") // 회사 대표
    val ceo: String? = "",
    @field:SerializedName("tel") // 회사 전화번호
    val tel: String? = "",
    @field:SerializedName("address") // 회사 주소
    val address: String? = "",
    @field:SerializedName("historyYear") // 연혁
    val historyYear: String? = "",
    @field:SerializedName("businessContent") // 사업 내용
    val businessContent: String? = "",
    @field:SerializedName("companyType") // 기업 유형
    val companyType: String? = "",
    @field:SerializedName("businessType") // 기업 분야
    val businessType: String? = "",
    @field:SerializedName("contentImg") // 이미지
    val contentImg: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(link)
        parcel.writeString(description)
        parcel.writeValue(mtrLvPeriod)
        parcel.writeValue(mtrLvSalary)
        parcel.writeString(mtrLvIsSalary)
        parcel.writeValue(mtrSupMoney)
        parcel.writeString(mtrSupCondition)
        parcel.writeValue(telComDays)
        parcel.writeString(telComIsCan)
        parcel.writeValue(telComTime)
        parcel.writeString(subsdType)
        parcel.writeValue(subsdMoney)
        parcel.writeValue(minSalary)
        parcel.writeValue(maxSalary)
        parcel.writeValue(avgSalary)
        parcel.writeValue(salaryYear)
        parcel.writeValue(femalePeople)
        parcel.writeValue(malePeople)
        parcel.writeString(ceo)
        parcel.writeString(tel)
        parcel.writeString(address)
        parcel.writeString(historyYear)
        parcel.writeString(businessContent)
        parcel.writeString(companyType)
        parcel.writeString(contentImg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompanyResponses> {
        override fun createFromParcel(parcel: Parcel): CompanyResponses {
            return CompanyResponses(parcel)
        }

        override fun newArray(size: Int): Array<CompanyResponses?> {
            return arrayOfNulls(size)
        }
    }
}
