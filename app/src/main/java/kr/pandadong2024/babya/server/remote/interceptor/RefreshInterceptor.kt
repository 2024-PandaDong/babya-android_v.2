import android.util.Log
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

        Log.d("RefreshInterceptor", "result : ${response.code == 401 && !(urlPath == "/auth/login" || urlPath == "/auth/email-verify" || urlPath == "/auth/email-send" || urlPath == "/auth/join")}")
        Log.d("RefreshInterceptor", "urlPath : $urlPath")
        Log.d("RefreshInterceptor", "code : ${response.code == 401 }")
        Log.d("RefreshInterceptor", "/auth/email-verify : ${ urlPath == "/auth/email-verify"}")
        Log.d("RefreshInterceptor", "/auth/email-send : ${ urlPath == "/auth/email-send"}")
        Log.d("RefreshInterceptor", "/auth/join : ${ urlPath == "/auth/join"}")

        if ((tokenDao.tokenDao().getMembers().accessToken != "") && response.code == 401 && !(urlPath == "/auth/login" || urlPath == "/auth/email-verify" || urlPath == "/auth/email-send" || urlPath == "/auth/join")) {
            Log.d("RefreshInterceptor", "in funfun true")

            // runBlocking을 사용하여 비동기 코드를 동기적으로 처리
            val newAccessToken = runBlocking {
                var token: String = ""
                val result = kotlin.runCatching {
                    RetrofitBuilder.getLoginService().requestRefresh(
                        RefreshRequest(
                            refreshToken = tokenDao.tokenDao().getMembers().refreshToken
                        )
                    )
                }

                result.onSuccess { response ->
                    if (response.data != null) {
                        token = response.data
                    }
                }.onFailure {
                    it.printStackTrace()
                }
                token // 최종적으로 반환할 토큰
            }

            if (newAccessToken.isNotEmpty()) {
                // 토큰을 저장
                tokenDao.tokenDao().updateMember(
                    TokenEntity(
                        id = tokenDao.tokenDao().getMembers().id,
                        accessToken = newAccessToken,
                        refreshToken = tokenDao.tokenDao().getMembers().refreshToken,
                        email = tokenDao.tokenDao().getMembers().email
                    )
                )

                response.close()
                // 새로운 요청 생성
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest) // 새로운 요청으로 대체
            }
        }

        // 기존 응답 반환
        return response
    }
}
