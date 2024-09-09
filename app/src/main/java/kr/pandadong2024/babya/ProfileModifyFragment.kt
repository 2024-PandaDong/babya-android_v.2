package kr.pandadong2024.babya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.databinding.FragmentProfileModifyBinding


class ProfileModifyFragment : Fragment() {

    private var _binding: FragmentProfileModifyBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileModifyBinding.inflate(inflater, container, false)

        return binding.root
    }
}