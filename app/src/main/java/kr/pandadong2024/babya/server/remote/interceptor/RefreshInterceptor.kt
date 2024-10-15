package kr.pandadong2024.babya.server.remote.interceptor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenEntity
import kr.pandadong2024.babya.server.remote.request.RefreshRequest
import okhttp3.Interceptor
import okhttp3.Response

class RefreshInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val tokenDao = BabyaDB.getInstanceOrNull() ?: throw RuntimeException()
        val urlPath = response.request.url.toString().substring(35)
        if (response.code == 401 && !(urlPath == "/auth/login" || urlPath == "/auth/email-verify" || urlPath == "/auth/email-send" || urlPath == "/auth/join")) {
            CoroutineScope(Dispatchers.IO).launch {
                runBlocking {
                    val accessToken = async {
                        var token: String = ""
                        kotlin.runCatching {
                            RetrofitBuilder.getLoginService().requestRefresh(
                                RefreshRequest(
                                    refreshToken = tokenDao.tokenDao().getMembers().refreshToken
                                )
                            )
                        }.onSuccess { result ->
                            if (result.data != null) {
                                token = result.data
                            }
                        }.onFailure {
                            it.printStackTrace()
                        }
                        token
                    }
                    val newAccessToken = accessToken.await()
                    tokenDao.tokenDao().updateMember(
                        TokenEntity(
                            id = tokenDao.tokenDao().getMembers().id,
                            accessToken = newAccessToken,
                            refreshToken = tokenDao.tokenDao().getMembers().refreshToken,
                            email = tokenDao.tokenDao().getMembers().email
                        )
                    )
                }

            }
            response.close()
            val newRequest = chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${tokenDao.tokenDao().getMembers().accessToken}"
                )
                .build()
            return chain.proceed(newRequest)
        } else {
            response.close()
            return chain.proceed(response.request)
        }
    }
}