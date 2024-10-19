package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileModifyBinding
import kr.pandadong2024.babya.home.policy.getLocalByCode
import kr.pandadong2024.babya.home.profile.profileviewmodle.ProfileViewModel
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.util.shortToast


class ProfileModifyFragment : Fragment() {

    private var _binding: FragmentProfileModifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenDao: TokenDAO
    private val userViewModel by activityViewModels<ProfileViewModel>()
    var email: String = ""


    val TAG = "ProfileModifyFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileModifyBinding.inflate(inflater, container, false)
        binding.profileBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileModifyFragment_to_profileFragment)
        }

        userViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message != "") {
                requireContext().shortToast(message)
            }
        }

        userViewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.nameText.text = userData.nickname
            binding.marriageDayText.text = userData.marriedYears?.replace("-", ".") ?: "0000.00.00"
            binding.birthDateText.text =
                userData.birthDt?.replace("-", ".") ?: "0000.00.00"
            binding.emailText.text = email
            if (userData.children?.size != 0 && userData.children != null) {
                binding.marriageText.text = userData.children[0].name
            } else {
                binding.marriageText.visibility = View.GONE
                binding.marriageTitleText.visibility = View.GONE
                binding.birthNameTitleText.visibility = View.GONE
                binding.birthNameDateText.visibility = View.GONE
            }
            if (userData.dDay == 0 || userData.dDay == null) {
                binding.pregnancyText.visibility = View.GONE
                binding.pregnancyDayTitleText.visibility = View.GONE
            } else {
                binding.pregnancyText.text = "D-${userData.dDay}"
            }

            binding.editTextButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileModifyFragment_to_editProfileFragment)
            }
            binding.titleText.text = "${userData.nickname}님의 정보"


            if (userData.profileImg == null) {
                binding.profileImage.load(R.drawable.ic_basic_profile)
            } else {
                binding.profileImage.load(userData.profileImg)
            }
        }

        userViewModel.userLocalCode.observe(viewLifecycleOwner) {
            Log.d(TAG, "code : $it")
            binding.localText.text = getLocalByCode(it)
        }


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            tokenDao = BabyaDB.getInstance(requireContext())?.tokenDao()!!
            email = tokenDao.getMembers().email
        }
    }
}