package kr.pandadong2024.babya.server.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TokenEntity::class], version = 4)
abstract class BabyaDB : RoomDatabase() {
    abstract fun tokenDao(): TokenDAO
    companion object {

        private var instance: BabyaDB? = null

        @Synchronized
        fun getInstance(context: Context): BabyaDB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    BabyaDB::class.java, "database")
//                    .addMigrations(MIGRATION_3_4)
                    .build()
            }
            return instance
        }

//        private val MIGRATION_3_4 = object : Migration(3,4){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE 'token_table' ADD COLUMN 'accessToken' INTEGER" )
//            }
//        }

        fun getInstanceOrNull(): BabyaDB? {
            return instance
        }
    }
}