package kr.pandadong2024.babya.server.remote.interceptor

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenEntity
import okhttp3.Interceptor
import okhttp3.Response

class RefreshInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        //response 로 부터 items 영역을 추출한다.
        Log.d("RefreshInterceptor", "response : $response")
        if (response.code == 401) {
            BabyaDB.getInstanceOrNull()?: throw RuntimeException()
            val tokenDao = BabyaDB.getInstanceOrNull()?: throw RuntimeException()
            CoroutineScope(Dispatchers.IO).launch {
                runBlocking {
                    val accessToken = async {
                        var token: String = ""
                        kotlin.runCatching {
                            RetrofitBuilder.getCommonService().requestRefresh(tokenDao.tokenDao().getMembers().refreshToken)
                        }.onSuccess { result ->
                            if (result.data != null){
                                token = result.data

                            }
                        }.onFailure {
                            it.printStackTrace()
                        }
                        token
                    }

                    tokenDao.tokenDao().updateMember(TokenEntity(
                        id = tokenDao.tokenDao().getMembers().id,
                        accessToken = accessToken.await(),
                        refreshToken = tokenDao.tokenDao().getMembers().refreshToken,
                        email = tokenDao.tokenDao().getMembers().email
                    ))
                }
            }

        }
        response.close()
        return chain.proceed(chain.request())
    }
}