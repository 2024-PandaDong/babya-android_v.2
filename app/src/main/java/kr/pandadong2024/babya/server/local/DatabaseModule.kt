package kr.pandadong2024.babya.server.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.local.DAO.UserDAO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BabyaDB {
        return Room.databaseBuilder(
            context.applicationContext,
            BabyaDB::class.java,
            "database"
        )
            .addMigrations(BabyaDB.MIGRATION_5_8)
            .addMigrations(BabyaDB.MIGRATION_7_8)
            .build()
    }

    @Provides
    fun provideTokenDao(datebase: BabyaDB): TokenDAO{
        return datebase.tokenDao()
    }

    @Provides
    fun provideUserDao(database: BabyaDB): UserDAO {
        return database.userDao()
    }
}