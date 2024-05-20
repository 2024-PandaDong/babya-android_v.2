package kr.pandadong2024.babya.server

import com.babya.server.service.LoginService
import kr.pandadong2024.babya.server.service.SignupService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kr.pandadong2024.babya.server.service.MainService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    companion object{
        private var gson: Gson? = null
        private var retrofit: Retrofit? = null
        private var loginService: LoginService? = null
        private var signupService: SignupService? = null
        private var mainService: MainService? = null

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

        fun getLoginService(): LoginService{
            if (loginService == null){
                loginService = getRetrofit().create(LoginService::class.java)
            }
            return loginService!!
        }

        fun getSignupService(): SignupService {
            if (signupService == null){
                signupService = getRetrofit().create(SignupService::class.java)
            }
            return signupService!!
        }
        fun getMainService() : MainService{
            if (mainService == null){
                mainService = getRetrofit().create(MainService::class.java)
            }
            return mainService!!
        }
    }
}