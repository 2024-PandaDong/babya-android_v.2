package kr.pandadong2024.babya.proto



import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.example.babya_android.datastore.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserRepository(private val dataStore: DataStore<User>) {
    val flow : Flow<User> = dataStore.data
//    val userFlow: Flow<User> = flow
        .catch { exception ->
            if (exception is IOException) {
                emit(User.getDefaultInstance())
            } else {
                throw exception
            }
        }
    suspend fun updateUserData(accessToken : String, refreshToken : String) {
        dataStore.updateData { items ->
            items.toBuilder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build()
        }
    }

    suspend fun clearUserData(){
        dataStore.updateData { items ->
            items
                .toBuilder()
                .clearAccessToken()
                .clearRefreshToken()
                .build()
        }
    }
}