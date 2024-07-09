package kr.pandadong2024.babya.home.diary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryBannerAdapter
import kr.pandadong2024.babya.home.diary.diaryadapters.DiaryMainGridViewAdapter
import kr.pandadong2024.babya.home.diary.diaryviewmodle.DiaryViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.diary.DiaryDataResponses
import kr.pandadong2024.babya.util.BottomControllable

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private var diaryList: List<DiaryDataResponses>? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiaryViewModel>()
    private lateinit var tokenDao: TokenDAO
    private var isPublic = true

    private lateinit var diaryMainGridViewAdapter: DiaryMainGridViewAdapter
    private lateinit var diaryBannerAdapter: DiaryBannerAdapter

    private var myEmail:String = ""

    private val TAG = "DiaryFragment"
    init {
        isPublic = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        initDiaryBannerView()

        binding.diaryDisclosureButton.setOnClickListener {
            Log.d(TAG, isPublic.toString())
            changeGridView()
        }


        binding.diaryEditFloatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_diaryFragment_to_editDiaryFragment)
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_diaryFragment_to_mainFragment)
        }
        binding.radioGroup.check(R.id.diaryAllRadio)
        when(binding.radioGroup.checkedRadioButtonId){
            binding.diaryAllRadio.id -> {
                getDiaryData(1, 100, 2)
            }

            binding.diaryMyRadio.id -> {
                getDiaryData(1, 100, 1)
            }

        }
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkId ->
            Log.d(TAG, "in setOnCheckedChangeListener")
            when (checkId) {
                binding.diaryAllRadio.id -> {
                    binding.diaryDisclosureButton.visibility = View.GONE
                    getDiaryData(1, 100, 2)
                }

                binding.diaryMyRadio.id -> {
                    binding.diaryDisclosureButton.visibility = View.VISIBLE
                    getDiaryData(1, 100, 1)
                }
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }




    private fun initDiaryGridView() {

        diaryMainGridViewAdapter = DiaryMainGridViewAdapter(diaryList!!) { diaryId, memberId  ->
            lifecycleScope.launch(Dispatchers.Main){
                kotlin.runCatching {
                    viewModel.id.value = diaryId
                    if (memberId == myEmail) {
                        findNavController().navigate(R.id.action_diaryFragment_to_detailWriterFragment)
                    } else {
                        findNavController().navigate(R.id.action_diaryFragment_to_detailPublicFragment)
                    }
                }.onFailure {
                    it.printStackTrace()
                }.onSuccess {

                }
            }


        }
        diaryMainGridViewAdapter.notifyDataSetChanged()
        binding.DiaryGridView.adapter = diaryMainGridViewAdapter
    }

    private fun changeGridView(){
        val changeList = mutableListOf<DiaryDataResponses>()
        when(isPublic){
            true ->{
                binding.diaryDisclosureText.setText("비공개")
                binding.diaryDisclosureIcon.setImageResource(R.drawable.ic_lock)
                diaryList?.forEach {
                    if (it.isPublic == "Y"){
                        changeList.add(it)
                    }
                }
            }
            false ->{
                binding.diaryDisclosureText.setText("공개")
                binding.diaryDisclosureIcon.setImageResource(R.drawable.ic_unlock)
                diaryList?.forEach {
                    if (it.isPublic == "N"){
                        changeList.add(it)
                    }
                }
            }
        }
        isPublic = isPublic.not()
        diaryMainGridViewAdapter.setDiaryList(changeList)
        diaryMainGridViewAdapter.notifyDataSetChanged()
    }

    private fun initDiaryBannerView() {
        diaryBannerAdapter = DiaryBannerAdapter(listOf(""), requireContext())
        diaryBannerAdapter.notifyItemRemoved(0)
        with(binding) {
            binding.diaryBannerViewPager.adapter = diaryBannerAdapter
            diaryBannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }


    // type | 1 : my | 2 : all
    private fun getDiaryData(
        page: Int,
        size: Int,
        type : Int
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                var diaryData : BaseResponse<List<DiaryDataResponses>>? = null
                myEmail = tokenDao.getMembers().email
                Log.d(TAG, "email : ${tokenDao.getMembers().email}")

                when(type){
                    1 -> {
                        diaryData = RetrofitBuilder.getDiaryService().getMyDiaryData(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page =  page,
                            size = size
                        )

                    }
                    2 -> {
                        isPublic = true
                        diaryData = RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size= size

                        )
                    }

                    else -> {
                        RetrofitBuilder.getDiaryService().getDiaryList(
                            accessToken = "Bearer ${tokenDao.getMembers().accessToken}",
                            page = page,
                            size= size
                        )
                    }
                }
                Log.d(TAG, "status : ${diaryData?.status}, message : ${diaryData?.message}")
                diaryList = diaryData?.data
            }.onFailure {
                Log.d(TAG, "${it.message}")
                diaryList = listOf(
                    DiaryDataResponses()
                )
                lifecycleScope.launch(Dispatchers.Main) {
                    initDiaryGridView()
                }
            }.onSuccess {
                lifecycleScope.launch(Dispatchers.Main) {
                    initDiaryGridView()
                    if(type == 1){
                        delay(1)
                        changeGridView()
                    }
                }
            }
        }

    }
}