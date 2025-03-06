package kr.pandadong2024.babya;

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kr.pandadong2024.babya.databinding.ActivityHomeBinding
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), BottomControllable {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)


        setContentView(binding.root)
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
        val navView: BottomNavigationView = binding.navView

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment

        commonViewModel.toastMessage.observe(this) { message ->
            if (message != "") {
                this.shortToast(message)
            }
        }

        navView.setupWithNavController(navController.navController)
    }

    override fun setBottomNavVisibility(visibility: Boolean) {
        findViewById<BottomNavigationView>(R.id.nav_view).visibility =
            if (visibility) View.VISIBLE else View.GONE
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}