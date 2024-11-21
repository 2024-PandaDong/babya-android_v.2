package kr.pandadong2024.babya.server.local.DAO


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kr.pandadong2024.babya.server.local.entity.UserEntity

@Dao
interface UserDAO {
    @Query("SELECT * FROM users_table")
    fun getMembers(): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(entity: UserEntity)

    @Update
    fun updateMember(entity: UserEntity)

    @Delete
    fun deleteMember(entity: UserEntity)
}