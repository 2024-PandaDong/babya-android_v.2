package kr.pandadong2024.babya.home.find_company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentFindCompanyBinding
import kr.pandadong2024.babya.databinding.FragmentMainBinding
import kr.pandadong2024.babya.util.BottomControllable

class FindCompanyFragment : Fragment() {
    private var _binding: FragmentFindCompanyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindCompanyBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.findCompanyBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_findCompanyFragment_to_mainFragment)
        }

        return return  binding.root
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}