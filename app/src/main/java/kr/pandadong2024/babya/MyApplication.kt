package kr.pandadong2024.babya

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import kr.pandadong2024.babya.server.Url

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        prefs = SharedPreferences(applicationContext)
        super.onCreate()

        KakaoMapSdk.init(this, Url.appKey)
    }
}