package kr.pandadong2024.babya.server.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.pandadong2024.babya.server.responses.ProfileChildrenData

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey
    val email: String,

    @ColumnInfo("nickname")
    val nickname: String?,

    @ColumnInfo("dDay")
    val dDay: String?,

    @ColumnInfo("birthDt")
    val birthDt: String?,

    @ColumnInfo("marriedYears")
    val marriedYears: String?,

    @ColumnInfo("children")
    val children: String?,

    @ColumnInfo("localCode")
    val localCode: String?,

    @ColumnInfo("profileImg")
    val profileImg: String?,
)