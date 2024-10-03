package kr.pandadong2024.babya.home.policy.bottom_sheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.PolicyBottomSheetBinding
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel

class PolicyBottomSheet(val submit : (tag : String)->Unit
) : BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<PolicyViewModel>()
    private var _binding: PolicyBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val zoneList = listOf(
        "서울특별시",
        "부산광역시",
        "대구광역시",
        "인천광역시",
        "광주광역시",
        "대전광역시",
        "울산광역시",
        "세종특별자치시",
        "경기도",
        "강원특별자치도",
        "충청북도",
        "충청남도",
        "전북특별자치도",
        "전라남도",
        "경상북도",
        "경상남도",
        "제주특별자치도"

    )


    private val countyList = mapOf<String, List<String>>(
        "대구광역시" to listOf("남구", "달서구", "달성군", "동구", "북구", "서구", "수성구", "중구", "군위군",),
        "부산광역시" to listOf("강서구", "금정구", "기장군", "남구", "동구", "동래구", "부산진구", "북구",
            "사상구", "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구"),
        "서울특별시" to listOf("강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
            "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
            "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"),
        "인천광역시" to listOf("강화군", "계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구",
            "옹진군", "중구"),
        "광주광역시" to listOf( "광산구", "남구", "동구", "북구", "서구"),
        "대전광역시" to listOf( "대덕구", "동구", "서구", "유성구", "중구"),
        "울산광역시" to listOf("남구", "동구", "북구", "울주군", "중구"),
        "세종특별자치시" to listOf("세종특별자치시"),
        "경기도" to listOf("가평군", "고양시", "고양시 덕양구", "고양시 일산동구", "고양시 일산서구", "과천시", "광명시",
            "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시", "부천시", "부천시 소사구",
            "부천시 오정구", "부천시 원미구", "성남시", "성남시 분당구", "성남시 수정구", "성남시 중원구",
            "수원시", "수원시 권선구", "수원시 영통구", "수원시 장안구", "수원시 팔달구", "시흥시",
            "안산시", "안산시 단원구", "안산시 상록구", "안성시", "안양시", "안양시 동안구", "안양시 만안구",
            "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "용인시 기흥구", "용인시 수지구",
            "용인시 처인구", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시", "화성시"),
        "강원특별자치도" to listOf( "강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군", "양양군",
            "영월군", "원주시", "인제군", "정선군", "철원군", "춘천시", "태백시", "평창군",
            "홍천군", "화천군", "횡성군"),
        "충청북도" to listOf( "괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "제천시",
            "증평군", "진천군", "청원군", "청주시", "청주시 상당구", "청주시 흥덕구", "충주시",
            "청주시 청원구", "청주시 서원구"),
        "충청남도" to listOf("계룡시", "공주시", "금산군", "논산시", "당진시", "보령시", "부여군",
            "서산시", "서천군", "아산시", "연기군", "예산군", "천안시", "천안시 동남구",
            "천안시 서북구", "청양군", "태안군", "홍성군"),
        "전북특별자치도" to listOf("고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군",
            "완주군", "익산시", "임실군", "장수군", "전주시", "전주시 덕진구", "전주시 완산구",
            "정읍시", "진안군"),
        "전라남도" to listOf("강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군",
            "목포시", "무안군", "보성군", "순천시", "신안군", "여수시", "영광군", "영암군",
            "완도군", "장성군", "장흥군", "진도군", "함평군", "해남군", "화순군"),
        "경상북도" to  listOf("경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군",
            "상주시", "성주군", "안동시", "영덕군", "영양군", "영주시", "영천시", "예천군",
            "울릉군", "울진군", "의성군", "청도군", "청송군", "칠곡군", "포항시", "포항시 남구", "포항시 북구"),
        "경상남도" to  listOf("거제시", "거창군", "고성군", "김해시", "남해군", "마산회원구", "마산합포구",
            "성산구", "의창구", "밀양시", "사천시", "산청군", "양산시", "의령군",
            "진주시", "진해구", "창녕군", "창원시", "통영시", "하동군", "함안군", "함양군", "합천군"),
        "제주특별자치도" to listOf( "서귀포시", "제주시")

    )

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val bottomSheet =
                dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            bottomSheet.layoutParams.height = 800
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PolicyBottomSheetBinding.inflate(inflater, container, false)

        binding.searchButton.setOnClickListener {
            submit(viewModel.tagsList.value!![1])
            if(viewModel.tagsList.value!!.isEmpty() ){
                viewModel.initViewModel()
            }


            this.dismiss()
        }
        viewModel.tagsList.observe(viewLifecycleOwner){
            binding.searchButton.isEnabled = it.size > 1
        }
        viewModel.tagsList.observe(viewLifecycleOwner) {
            initZoneRecyclerview(it)
            if (it.isNotEmpty()) {
                if (countyList.keys.contains(it[0])) {
                    binding.localTitle.visibility = View.VISIBLE
                    binding.localChipGroup.visibility = View.VISIBLE
                    initSubZoneRecyclerView(
                        countyList[it[0]]!!,
                        it
                    )
                } else {
                    binding.localTitle.visibility = View.GONE
                    binding.localChipGroup.visibility = View.GONE
                }
            }else {
                binding.localTitle.visibility = View.GONE
                binding.localChipGroup.visibility = View.GONE
            }

        }

        return binding.root
    }

    private fun initZoneRecyclerview(selectedTag: List<String>) {
        binding.ZoneChipGroup.removeAllViews()
        val keyWord = if (selectedTag.isEmpty()) {
            ""
        } else {
            selectedTag[0]
        }
        zoneList.forEach {
            val chipGroup = binding.ZoneChipGroup

            chipGroup.addView(Chip(requireContext()).apply {
                text = it  // text 세팅
                isCheckable = true
                isChecked = (it == keyWord)
                setChipBackgroundColorResource(R.color.backgroundNormalNormal)
                setTextColor(resources.getColorStateList(R.color.chip_color, null))
                setChipStrokeColorResource(R.color.chip_color)
                chipCornerRadius = 31f
                chipStrokeWidth = 3f

                setOnClickListener { view ->
                    if (keyWord == "") {
                        viewModel.inputLocal(it)
                    } else {
                        viewModel.removeAll()
                        if (it == keyWord) {
                            viewModel.popLocal(keyWord)
                        } else {
                            viewModel.inputLocal(it)
                        }
                    }
                }
            })
        }
    }

    private fun initSubZoneRecyclerView(subTagList: List<String>, selectedTags: List<String>) {
        binding.localChipGroup.removeAllViews()

        val keyWord = if (subTagList.isEmpty()) {
            ""
        } else {
            selectedTags[0]
        }

        subTagList.forEach { localTag ->
            val localChipGroup = binding.localChipGroup

            localChipGroup.addView(Chip(requireContext()).apply {
                text = localTag  // text 세팅
                isCheckable = true
                isChecked = localTag in selectedTags && selectedTags.size >= 2
                setChipBackgroundColorResource(R.color.backgroundNormalNormal)
                setTextColor(resources.getColorStateList(R.color.chip_color, null))
                setChipStrokeColorResource(R.color.chip_color)
                chipCornerRadius = 31f
                chipStrokeWidth = 3f

                setOnClickListener { view ->
                    if (keyWord == "") {
                        viewModel.inputLocal(localTag)
                        Log.d("test", "1")
                    } else {
                        viewModel.removeSubTags()
                        if (localTag == keyWord && (subTagList.size > 1)) {
                            Log.d("test", "3")
                            viewModel.popLocal(keyWord)
                        } else {
                            viewModel.inputLocal(localTag)
                            Log.d("test", "2")
                        }
                    }
                }
            })

        }
    }


    private fun encodingLocateNumber(locationList: List<String>): MutableList<String> {
        val result = mutableListOf<String>()
        locationList.forEach {
            when (it) {
                "남구" -> result.add("104010")
                "달서구" -> result.add("104020")
                "달성군" -> result.add("104030")
                "동구" -> result.add("104040")
                "북구" -> result.add("104050")
                "서구" -> result.add("104060")
                "수성구" -> result.add("104070")
                "중구" -> result.add("104080")
                "군위군" -> result.add("104090")
            }
        }
        return result
    }

    private fun decodingLocateNumber(locationList: List<String>): MutableList<String> {
        val result = mutableListOf<String>()
        locationList.forEach {
            when (it) {
                "11" -> result.add("서울특별시")
                "21" -> result.add("부산광역시")
                "22" -> result.add("대구광역시")
                "23" -> result.add("인천광역시")
                "24" -> result.add("광주광역시")
                "25" -> result.add("대전광역시")
                "26" -> result.add("울산광역시")
                "31" -> result.add("경기도")
                "29" -> result.add("세종특별자치시")
                "32" -> result.add("강원도")
                "33" -> result.add("충청북도")
                "34" -> result.add("충청남도")
                "35" -> result.add("전라북도")
                "36" -> result.add("전라남도")
                "37" -> result.add("경상북도")
                "38" -> result.add("경상남도")
                "39" -> result.add("제주특별자치도")
            }
        }
        return result
    }
}