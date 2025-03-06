package kr.pandadong2024.babya;

import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.android.identity.android.legacy.Utility
import dagger.hilt.android.AndroidEntryPoint
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.util.shortToast
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var commonViewModel: CommonViewModel
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
        commonViewModel.toastMessage.observe(this) { message ->
            if (message != "") {
                this.shortToast(message)
            }
        }
    }
}