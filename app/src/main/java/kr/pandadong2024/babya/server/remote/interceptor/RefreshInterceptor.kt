package kr.pandadong2024.babya.server.remote.interceptor

import android.util.Log
import coil.network.HttpException
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
        Log.i("RefreshInterceptor", "response : $response")
        Log.i("RefreshInterceptor", "accessToken : ${tokenDao.tokenDao().getMembers().accessToken}")
        Log.i(
            "RefreshInterceptor",
            "refreshToken : ${tokenDao.tokenDao().getMembers().refreshToken}"
        )
        if (response.code == 401) {
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
                        if (it is HttpException) {
                            Log.e("err", "err : ${it.response.body}")
                        }
                    }
                    token
                }
                val newAccessToken = accessToken.await()
                Log.i("RefreshInterceptor", "newAccessToken : $newAccessToken")
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
        }
        response.close()

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenDao.tokenDao().getMembers().accessToken}")
            .build()
        return chain.proceed(newRequest)
    }
}