package kr.pandadong2024.babya.home.policy.bottom_sheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.PolicyBottomSheetBinding
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel

class PolicyBottomSheet(
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


    val countyList = mapOf<String, List<String>>(
        "대구광역시" to listOf(
            "동구",
            "서구",
            "남구",
            "북구",
            "중구",
            "수성구",
            "군위군",
            "달성군"
        ), "부산광역시" to listOf("수영구", "동래구")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PolicyBottomSheetBinding.inflate(inflater, container, false)

        binding.searchButton.setOnClickListener {
            this.dismiss()
        }

        viewModel.tagsList.observe(viewLifecycleOwner) {

            initZoneRecyclerview(viewModel.tagsList.value!!)
            if (viewModel.tagsList.value!!.isNotEmpty()) {
                if (countyList.keys.contains(viewModel.tagsList.value!![0])) {
                    binding.localTitle.visibility = View.VISIBLE
                    binding.localChipGroup.visibility = View.VISIBLE
                    initSubZoneRecyclerView(
                        countyList[viewModel.tagsList.value!![0]]!!,
                        viewModel.tagsList.value!!
                    )
                } else {
                    binding.localTitle.visibility = View.GONE
                    binding.localChipGroup.visibility = View.GONE
                }
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
        Log.d("TAG", "lsit : $zoneList")
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
                    Log.d("test", "isChecked = ${(keyWord)}")
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
        subTagList.forEach { localTag ->
            val localChipGroup = binding.localChipGroup

            localChipGroup.addView(Chip(requireContext()).apply {
                text = localTag  // text 세팅
                isCheckable = true
                isChecked = localTag in selectedTags
                setChipBackgroundColorResource(R.color.backgroundNormalNormal)
                setTextColor(resources.getColorStateList(R.color.chip_color, null))
                setChipStrokeColorResource(R.color.chip_color)
                chipCornerRadius = 31f
                chipStrokeWidth = 3f

                setOnClickListener { view ->

                    if (binding.root.findViewById<Chip>(view.id).isChecked) {
                        viewModel.inputLocal(localTag)
                    } else {
                        viewModel.popLocal(localTag)
                    }
                }
            })

        }
    }

    private fun encodingSelected(selectedList: List<String>): SubmitList {
        val mainSelectResult = mutableListOf<String>()
        val subList = mutableListOf<List<String>>()
        val subSelectResult = mutableListOf<String>()

        selectedList.forEach {
            Log.d("test", "subList1 = ${it in zoneList}")
            Log.d("test", "subList2 = ${zoneList.contains(it)}")
            if (it in zoneList) {
                mainSelectResult.add(it)
            } else {
                Log.e("PolicyBottomSheet", "에러 : 존재하지 않는 태그")
            }
        }
        if (mainSelectResult.size != 0) {
            mainSelectResult.forEach { it ->
                Log.d("test", "subList3 = $it")
                Log.d("test", "subList4 = ${it}")

                if (countyList.keys.contains(it)) {
                    subList += countyList[it]!!
                    Log.d("test", "subList5 = $subList")

                    selectedList.forEach { it1 ->
                        Log.d("test", "subList1 = ${it1 in zoneList}")
                        Log.d("test", "subList2 = ${zoneList.contains(it1)}")
                        if (it1 in subList[subList.lastIndex]) {
                            subSelectResult.add(it1)
                        } else {
                            Log.e("PolicyBottomSheet", "에러 : 존재하지 않거나 다른지역의 태그")
                        }
                    }
                }
            }
        } else {
            Log.e("PolicyBottomSheet", "에러 : 존재할 수 없는 상황")
        }





        return SubmitList(
            mainTagSelectedList = mainSelectResult,
            subTagSelectedList = subSelectResult,
            subTagList = subList
        )

    }

    private fun encodingLocateNumber(locationList: List<String>): MutableList<String> {
        val result = mutableListOf<String>()
        locationList.forEach {
            when (it) {
                "서울특별시" -> result.add("11")
                "부산광역시" -> result.add("21")
                "대구광역시" -> result.add("22")
                "인천광역시" -> result.add("23")
                "광주광역시" -> result.add("24")
                "대전광역시" -> result.add("25")
                "울산광역시" -> result.add("26")
                "경기도" -> result.add("31")
                "세종특별자치시" -> result.add("29")
                "강원도" -> result.add("32")
                "충청북도" -> result.add("33")
                "충청남도" -> result.add("34")
                "전라북도" -> result.add("35")
                "전라남도" -> result.add("36")
                "경상북도" -> result.add("37")
                "경상남도" -> result.add("38")
                "제주특별자치도" -> result.add("39")
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


    data class SubmitList(
        var mainTagSelectedList: List<String>,
        val subTagSelectedList: List<String>,
        val subTagList: List<List<String>>
    )


}