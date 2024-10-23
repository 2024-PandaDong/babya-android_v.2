package kr.pandadong2024.babya;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.util.shortToast

class MainActivity : AppCompatActivity() {
    private lateinit var commonViewModel: CommonViewModel
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