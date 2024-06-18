package kr.pandadong2024.babya.server

import com.babya.server.service.LoginService
import kr.pandadong2024.babya.server.service.SignupService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kr.pandadong2024.babya.server.service.ProfileService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitBuilder {
    companion object{
        private var gson: Gson? = null
        private var retrofit: Retrofit? = null
        private var loginService: LoginService? = null
        private var signupService: SignupService? = null
        private var profileService: ProfileService? = null

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
        fun getProfileService(): ProfileService{
            if (profileService == null){
                profileService = getRetrofit().create(ProfileService::class.java)
            }
            return profileService!!
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
    }
}