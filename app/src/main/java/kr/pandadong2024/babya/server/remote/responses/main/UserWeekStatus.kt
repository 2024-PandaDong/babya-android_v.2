package kr.pandadong2024.babya.server.remote.responses.main

import com.google.gson.annotations.SerializedName

data class UserWeekStatus (
    @field:SerializedName("c1")
    val c1: Int? = 0,
    @field:SerializedName("c2")
    val c2: Int? = 0,
    @field:SerializedName("c3")
    val c3: Int? = 0,
    @field:SerializedName("c4")
    val c4: Int? = 0,
    @field:SerializedName("c5")
    val c5: Int? = 0,
)