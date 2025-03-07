package kr.pandadong2024.babya.server.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.DAO.UserDAO
import kr.pandadong2024.babya.server.local.converter.ChildrenListTypeConverter
import kr.pandadong2024.babya.server.local.entity.TokenEntity
import kr.pandadong2024.babya.server.local.entity.UserEntity

@Database(entities = [TokenEntity::class,UserEntity::class], version = 8)
@TypeConverters(ChildrenListTypeConverter::class)
abstract class BabyaDB : RoomDatabase() {
    abstract fun tokenDao(): TokenDAO
    abstract fun userDao(): UserDAO


    companion object {

//        private var instance: BabyaDB? = null
//
//
//        @Synchronized
//        fun getInstance(context: Context): BabyaDB? {
//
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    BabyaDB::class.java, "database"
//                )
//                    .addMigrations(MIGRATION_7_8)
//                    .addMigrations(MIGRATION_5_8)
//                    .build()
//            }
//            return instance
//        }

         val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE 'user_table' ADD COLUMN 'localCode' TEXT NOT NULL DEFAULT ''"
                )
//                db.execSQL(
//                    """
//            CREATE TABLE users_table (
//                email TEXT NOT NULL PRIMARY KEY,
//                nickname TEXT,
//                dDay TEXT,
//                birthDt TEXT,
//                marriedYears TEXT,
//                children TEXT,
//                profileImg TEXT
//            )
//            """
//                )

            }
        }

         val MIGRATION_5_8 = object : Migration(5, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
            CREATE TABLE users_table (
                email TEXT NOT NULL PRIMARY KEY,
                nickname TEXT,
                dDay TEXT,
                birthDt TEXT,
                marriedYears TEXT,
                children TEXT,
                profileImg TEXT,
                localCode TEXT
            )
            """
                )
//                db.execSQL(
//                    "ALTER TABLE 'user_table' ADD COLUMN 'localCode' TEXT NOT NULL DEFAULT ''"
//                )

            }
        }


//        fun getInstanceOrNull(): BabyaDB? {
//            return instance
//        }
    }
}