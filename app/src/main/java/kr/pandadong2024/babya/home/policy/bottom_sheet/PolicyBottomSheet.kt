package kr.pandadong2024.babya.home.policy.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.BottomSheetBinding

class PolicyBottomSheet(
    tagList: MutableList<String>,
    submit: (List<String>) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return  binding.root
    }
}