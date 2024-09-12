package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup8Binding
import kr.pandadong2024.babya.databinding.FragmentSignup9Binding
import kr.pandadong2024.babya.start.viewmodel.SignupViewModel

class Signup9 : Fragment() {

    private var _binding: FragmentSignup9Binding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup9Binding.inflate(inflater, container, false)

        kotlin.run {
            Log.d(TAG, "email: ${viewModel.email.value}")
            Log.d(TAG, "pw: ${viewModel.pw.value}")
            Log.d(TAG, "nickName: ${viewModel.nickName.value}")
            Log.d(TAG, "marriedDt: ${viewModel.marriedDt.value}")
            Log.d(TAG, "pregnancyDt: ${viewModel.pregnancyDt.value}")
            Log.d(TAG, "birthDt: ${viewModel.birthDt.value}")
            Log.d(TAG, "locationCode: ${viewModel.locationCode.value}")
            Log.d(TAG, "pushToken: ${viewModel.pushToken.value}")
            Log.d(TAG, "birthNameList: ${viewModel.birthNameList.value}")
            Log.d(TAG, "childrenNameList: ${viewModel.childrenNameList.value}")

        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup9_to_signup5)
        }


        return binding.root
    }

}