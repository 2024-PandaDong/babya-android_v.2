package kr.pandadong2024.babya.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.interceptor.RefreshInterceptor
import kr.pandadong2024.babya.server.remote.interceptor.TokenInterceptor
import kr.pandadong2024.babya.server.remote.service.CommonService
import kr.pandadong2024.babya.server.remote.service.CompanyService
import kr.pandadong2024.babya.server.remote.service.DashBoardService
import kr.pandadong2024.babya.server.remote.service.DiaryService
import kr.pandadong2024.babya.server.remote.service.LoginService
import kr.pandadong2024.babya.server.remote.service.MainService
import kr.pandadong2024.babya.server.remote.service.PolicyService
import kr.pandadong2024.babya.server.remote.service.QuizService
import kr.pandadong2024.babya.server.remote.service.SignupService
import kr.pandadong2024.babya.server.remote.service.TodoListService
import kr.pandadong2024.babya.server.service.ProfileService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitBuilder {
    companion object {
        private var gson: Gson? = null
        private var retrofit: Retrofit? = null
        private var loginService: LoginService? = null
        private var signupService: SignupService? = null
        private var commonService: CommonService? = null
        private var policyService: PolicyService? = null
        private var mainService: MainService? = null
        private var todoListService: TodoListService? = null
        private var diaryService: DiaryService? = null
        private var httpClient: OkHttpClient? = null
        private var tokenDao: TokenDAO? = null
        private var quizService: QuizService? = null
        private var profileService: ProfileService? = null
        private var dashBoardService: DashBoardService? = null
        private var companyService: CompanyService? = null

        @Synchronized
        fun getGson(): Gson? {
            if (gson == null) {
                gson = GsonBuilder().setLenient().create()
            }

            return gson
        }

        @Synchronized
        fun getRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Url.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create(getGson()!!))
                    .build()
            }
            return retrofit!!
        }

        @Synchronized
        fun getHttpRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Url.serverUrl)
                    .client(getOhHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(getGson()!!))
                    .build()
            }
            return retrofit!!
        }

        @Synchronized
        fun getHttpTokenRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Url.serverUrl)
                    .client(getTokenOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(getGson()!!))
                    .build()
            }
            return retrofit!!
        }

        @Synchronized
        fun getOhHttpClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val refreshInterceptor = RefreshInterceptor()
            tokenDao = BabyaDB.getInstanceOrNull()?.tokenDao()
            val httpClient = OkHttpClient.Builder().apply {

            }
//            httpClient.addNetworkInterceptor()
            return httpClient
                .addInterceptor(refreshInterceptor)
                .addInterceptor(interceptor)
                .build()
        }

        @Synchronized // 로그인 일회용
        fun getTokenOkHttpClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val okhttpBuilder = OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .addInterceptor(TokenInterceptor())


            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            okhttpBuilder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            okhttpBuilder.hostnameVerifier { hostname, session -> true }
            return okhttpBuilder.build()
        }

        fun getLoginService(): LoginService {
            if (loginService == null) {
                loginService = getRetrofit().create(LoginService::class.java)
            }
            return loginService!!
        }

        fun getCompanyService(): CompanyService {
            if (companyService == null) {
                companyService = getHttpRetrofit().create(CompanyService::class.java)
            }
            return companyService!!
        }

        fun getPolicyService(): PolicyService {
            if (policyService == null) {
                policyService = getRetrofit().create(PolicyService::class.java)
            }
            return policyService!!
        }

        fun getQuizService(): QuizService {
            if (quizService == null) {
                quizService = getHttpRetrofit().create(QuizService::class.java)
            }
            return quizService!!
        }

        fun getProfileService(): ProfileService {
            if (profileService == null) {
                profileService = getRetrofit().create(ProfileService::class.java)
            }
            return profileService!!
        }

        fun getSignupService(): SignupService {
            if (signupService == null) {
                signupService = getRetrofit().create(SignupService::class.java)
            }
            return signupService!!
        }

        fun getHttpMainService(): MainService {
            if (mainService == null) {
                mainService = getHttpRetrofit().create(MainService::class.java)
            }
            return mainService!!
        }

        fun getMainService(): MainService {
            if (mainService == null) {
                mainService = getRetrofit().create(MainService::class.java)
            }
            return mainService!!
        }

        fun getDiaryService(): DiaryService {
            if (diaryService == null) {
                diaryService = getHttpRetrofit().create(DiaryService::class.java)
            }
            return diaryService!!
        }

        fun getDashBoardService(): DashBoardService {
            if (dashBoardService == null) {
                dashBoardService = getHttpRetrofit().create(DashBoardService::class.java)
            }
            return dashBoardService!!
        }

        fun getTodoListService(): TodoListService {
            if (todoListService == null) {
                todoListService = getHttpRetrofit().create(TodoListService::class.java)
            }
            return todoListService!!
        }

        fun getCommonService(): CommonService {
            if (commonService == null) {
                commonService = getHttpRetrofit().create(CommonService::class.java)
            }
            return commonService!!
        }
    }
}