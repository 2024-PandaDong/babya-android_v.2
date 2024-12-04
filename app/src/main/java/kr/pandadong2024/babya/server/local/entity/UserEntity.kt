package kr.pandadong2024.babya.server.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey
    var email: String,

    @ColumnInfo("nickname")
    var nickname: String?,

    @ColumnInfo("dDay")
    var dDay: String?,

    @ColumnInfo("birthDt")
    var birthDt: String?,

    @ColumnInfo("marriedYears")
    var marriedYears: String?,

    @ColumnInfo("children")
    var children: String?,

    @ColumnInfo("localCode")
    var localCode: String?,

    @ColumnInfo("profileImg")
    var profileImg: String?,
)