package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.databinding.FragmentEditDiaryBinding
import kr.pandadong2024.babya.util.BottomControllable

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}