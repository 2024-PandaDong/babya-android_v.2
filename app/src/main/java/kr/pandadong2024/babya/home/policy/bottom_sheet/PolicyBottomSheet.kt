package kr.pandadong2024.babya.home.policy.bottom_sheet

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.PolicyBottomSheetBinding

class PolicyBottomSheet(
    tagList: MutableList<String>,
    submit: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: PolicyBottomSheetBinding? = null
    private val binding get() = _binding!!

    // bottomSheetBehavior


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PolicyBottomSheetBinding.inflate(inflater, container, false)




        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val bottomSheet: View = binding.bottomSheet
//        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // 처음에 접힌 상태로 시작


    }
}