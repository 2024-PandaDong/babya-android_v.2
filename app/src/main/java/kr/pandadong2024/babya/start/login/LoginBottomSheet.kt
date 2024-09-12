package kr.pandadong2024.babya.start.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.LoginBottomSheetBinding
import kr.pandadong2024.babya.databinding.PolicyBottomSheetBinding
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.util.setOnSingleClickListener
//todo : 에러 핸들링하기
class LoginBottomSheet(val login : (email : String, password : String)->Unit
) : BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<PolicyViewModel>()
    private var _binding: LoginBottomSheetBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginBottomSheetBinding.inflate(inflater, container, false)
        binding.loginBtn.setOnSingleClickListener {
            login("","") // TODO : 내부에 값 넣기
            dismiss()
        }
        return binding.root
    }
}