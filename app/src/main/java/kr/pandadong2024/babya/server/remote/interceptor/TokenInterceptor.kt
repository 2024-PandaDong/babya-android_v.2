package kr.pandadong2024.babya.server.remote.interceptor

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class TokenInterceptor : Interceptor {
    private val TOKEN_ERROR = 403
    private val TOKEN_HEADER = "Authorization"

    private lateinit var token: String
    private lateinit var tokenDao: TokenDAO


    override fun intercept(chain: Interceptor.Chain): Response {
        runBlocking(Dispatchers.IO) {
//            val dao = BabyaDB.getInstanceOrNull()!!
//            Log.d( "test", "${dao.tokenDao().getMembers()}")
            val dao = BabyaDB.getInstanceOrNull()?: throw RuntimeException()
            tokenDao = dao.tokenDao()
            token = tokenDao.getMembers().accessToken
        }
        val request = chain.request().newBuilder()
            .addHeader(TOKEN_HEADER, token)
            .build()

        Log.d("test", "in ${token}")

        var response = chain.proceed(request)
        if (response.code == TOKEN_ERROR) {
            response.close()
            runBlocking {
                val refreshToken = tokenDao.getMembers().refreshToken
                if (refreshToken.isEmpty()) {
                    tokenDao.deleteMember(tokenDao.getMembers())
                    response = Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(TOKEN_ERROR)
                        .message("세션이 만료되었습니다.")
                        .body("{\"status\":401,\"message\":\"세션이 만료되었습니다.\"}".toResponseBody())
                        .build()
                } else {
//                     재발급
//                    token = RetrofitBuilder.getTokenService().postRefreshToken(
//                        TokenRequest(refreshToken)
//                    ).accessToken


                    // 재발급된 토큰으로 재실행
                    val newToken = chain.request().newBuilder()
                        .addHeader(TOKEN_HEADER, "bearer $token")
                        .build()
                    response = chain.proceed(newToken)
                    if (response.code == TOKEN_ERROR) {
                        tokenDao.deleteMember(tokenDao.getMembers())
                        response = Response.Builder()
                            .request(request)
                            .protocol(Protocol.HTTP_1_1)
                            .code(TOKEN_ERROR)
                            .message("세션이 만료되었습니다.")
                            .body("{\"status\":401,\"message\":\"세션이 만료되었습니다.\"}".toResponseBody())
                            .build()
                    }
                }
            }
        }
        return response
    }
}