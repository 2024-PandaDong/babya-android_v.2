package kr.pandadong2024.babya.server.local.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kr.pandadong2024.babya.server.local.entity.TokenEntity

@Dao
interface TokenDAO {
    @Query("SELECT * FROM token_table")
    fun getMembers(): TokenEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(entity: TokenEntity)

    @Update
    fun updateMember(entity: TokenEntity)

    @Delete
    fun deleteMember(entity: TokenEntity)
}