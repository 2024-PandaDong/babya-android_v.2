package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileModifyBinding
import kr.pandadong2024.babya.home.viewmodel.CommonViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO


class ProfileModifyFragment : Fragment() {

    private var _binding: FragmentProfileModifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO
    private val commonViewModel by activityViewModels<CommonViewModel>()


    val TAG = "ProfileModifyFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileModifyBinding.inflate(inflater, container, false)
        binding.profileBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileModifyFragment_to_profileFragment)
        }
        getProfileData()


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            tokenDao = BabyaDB.getInstance(requireContext())?.tokenDao()!!
        }
    }

    private fun getProfileData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val token : String = tokenDao.getMembers().accessToken
            kotlin.runCatching {
                Log.d(TAG, "token : $token")
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer $token",
                    email = "my"
                )
            }.onSuccess { result ->
                if(result.status == 200) {
                    val email = tokenDao.getMembers().email

                    launch(Dispatchers.Main) {
                        binding.nameText.text = result.data?.nickname
                        binding.marriageDayText.text = "${result.data?.marriedYears}년"
                        binding.ageText.text = "${result.data?.birthDt}살"
                        binding.ageText.text = "D-${result.data?.dDay}"
                        binding.emailText.text = email
                        if (result.data?.children?.size != 0) {
                            binding.marriageText.text = result.data?.children!![0].name
                        } else {
                            binding.marriageText.visibility = View.GONE
                            binding.marriageTitleText.visibility = View.GONE
                        }

                        if (result.data.profileImg == null) {
                            binding.profileImage.load(R.drawable.ic_basic_profile)
                        } else {
                            binding.profileImage.load(result.data.profileImg)
                        }

                    }
                }
                else{
                    commonViewModel.setToastMessage( "데이터를 불러오는 도중 문제가 발생했습니다. CODE : ${result.status}")
                }
            }.onFailure { result ->
                Log.d(TAG, "onCreateView: ${result.message}")
                result.printStackTrace()
                commonViewModel.setToastMessage( "인터넷이 연결되어있는지 확인해 주십시오")
            }
        }
    }
}