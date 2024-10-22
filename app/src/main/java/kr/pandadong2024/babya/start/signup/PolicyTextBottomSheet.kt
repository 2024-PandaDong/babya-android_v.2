package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyTextBottomSheetBinding
import kr.pandadong2024.babya.databinding.FragmentSignupBottomSheetBinding

class PolicyTextBottomSheet() : BottomSheetDialogFragment() {

    private var _binding: FragmentPolicyTextBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPolicyTextBottomSheetBinding.inflate(inflater, container, false)






        binding.checkBtn.setOnClickListener {
            this.dismiss()
        }



        return binding.root
    }
}