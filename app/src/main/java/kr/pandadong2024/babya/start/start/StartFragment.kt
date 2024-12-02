package kr.pandadong2024.babya.start.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.HomeActivity
import kr.pandadong2024.babya.MyApplication.Companion.prefs
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentStartBinding
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.entity.UserEntity
import kr.pandadong2024.babya.util.shortToast
import java.text.SimpleDateFormat
import java.util.Locale

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var accessToken: String? = null

    private var userEntity: UserEntity = UserEntity(
        email = "",
        nickname = "",
        dDay = "",
        birthDt = "",
        marriedYears = "",
        children = "",
        localCode = "",
        profileImg = "",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            launch {
                val now = System.currentTimeMillis()
                val today = SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN).format(now)
                if (today != prefs.lastEditTime) {
                    prefs.lastEditTime = today
                    prefs.completeQuiz = false
                }
            }
            getAccessToken()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        profileViewModel.userData.observe(viewLifecycleOwner) {
            Log.d("dbTest", "user data : $it")
            userEntity.email = userEntity.email
            userEntity.nickname = it.nickname
            userEntity.dDay = it.dDay
            userEntity.birthDt = it.birthDt
            userEntity.marriedYears = it.marriedYears
            userEntity.children = it.children.toString()
            userEntity.profileImg = it.profileImg
            if (it.nickname?.isNotEmpty() == true) {
                saveUserData()
            }
        }

        profileViewModel.userLocalCode.observe(viewLifecycleOwner) {
            Log.d("dbTest", "local data : $it")
            if (it.length >= 3) {
                userEntity.localCode = it
                profileViewModel.getUserData()
            }
        }

        lifecycleScope.launch() {
            delay(1500)
            launch {
                Log.d("StartFragment", "accessToken : ${accessToken}")
                if ((accessToken != null) && accessToken!!.isNotEmpty()) {
                    updateUserData()
                } else {
                    requireContext().shortToast("세션이 만료되었습니다.")
                    findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                }
            }
        }

        return binding.root
    }

    private fun updateUserData(){
        profileViewModel.getUserLocalCode()
    }
    private fun saveUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
            BabyaDB.getInstance(requireContext())?.userDao()?.insertMember(userEntity)}.onSuccess {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                requireActivity().finish()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getAccessToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            accessToken = BabyaDB.getInstance(requireContext())?.tokenDao()
                ?.getMembers()?.accessToken
        }
    }
}