package kr.pandadong2024.babya.home.dash_board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDashBoardBinding
import kr.pandadong2024.babya.databinding.FragmentEditDashBoardBinding
import kr.pandadong2024.babya.util.BottomControllable


class EditDashBoardFragment : Fragment() {
    private var _binding: FragmentEditDashBoardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentEditDashBoardBinding.inflate(inflater, container, false)

        binding.dashBoardBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDashBoardFragment_to_dashBoardFragment)
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)

    }

}