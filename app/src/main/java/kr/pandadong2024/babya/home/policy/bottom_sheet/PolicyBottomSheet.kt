package kr.pandadong2024.babya.home.policy.bottom_sheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.PolicyBottomSheetBinding

class PolicyBottomSheet(
    private val tagList: MutableList<String>,
    val submit: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

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
        "경기도",
        "세종특별자치시",
        "강원도",
        "충청북도",
        "충청남도",
        "전라북도",
        "전라남도",
        "경상북도",
        "경상남도",
        "제주특별자치도"
    )

    val countyList = mapOf<String, List<String>>("대구광역시" to listOf("동구", "서구", "남구", "북구", "중구", "수성구", "군위군", "달성군"), "부산광역시" to listOf("수영구", "동래구"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PolicyBottomSheetBinding.inflate(inflater, container, false)

        val encodingSelects= encodingSelected(listOf("대구광역시", "동구", "부산광역시", "수영구", "동래구","중구", "수성구", "군위군"))
        Log.i("PolicyBottomSheet", "test : $encodingSelects")

        binding.searchButton.setOnClickListener {
            Log.d("PolicyBottomSheet", "list : $tagList")
            submit(tagList)

        }
        initZoneRecyclerview(encodingSelects)


        return binding.root
    }

    private fun initZoneRecyclerview(encodingSelects : SubmitList){
        zoneList.forEach {
            val chipGroup = binding.ZoneChipGroup

            chipGroup.addView(Chip(requireContext()).apply {
                text = it // text 세팅
                isCheckable = true
                setChipBackgroundColorResource(R.color.backgroundNormalNormal)
                setTextColor(resources.getColorStateList(R.color.chip_color, null))
                setChipStrokeColorResource(R.color.chip_color)
                chipCornerRadius = 31f
                chipStrokeWidth = 3f

                setOnClickListener { view ->

                    if (binding.root.findViewById<Chip>(view.id).isChecked) {
                        tagList.add(it)
                    }
                    else{
                        tagList.remove(it)
                    }
                }
            })
        }
    }

    private fun encodingSelected(selectedList : List<String>): SubmitList {
        val mainSelectResult = mutableListOf<Int>()
        val subList = mutableListOf<List<String>>()
        val subSelectResult = mutableListOf<List<Int>>()

        selectedList.forEach {
            Log.d("test", "subList1 = ${it in zoneList}")
            Log.d("test", "subList2 = ${zoneList.contains(it)}")
            if(it in zoneList){
                mainSelectResult.add(zoneList.indexOf(it))
            }
            else{
                Log.e("PolicyBottomSheet", "에러 : 존재하지 않는 태그")
            }
        }
        if(mainSelectResult.size != 0)
        mainSelectResult.forEach{ it ->
            Log.d("test", "subList3 = $it")
            Log.d("test", "subList4 = ${zoneList[it]}")

            subList += countyList[zoneList[it]]!!
            Log.d("test", "subList5 = $subList")

            val subResult = mutableListOf<Int>()
            selectedList.forEach { it1 ->
                Log.d("test", "subList1 = ${it1 in zoneList}")
                Log.d("test", "subList2 = ${zoneList.contains(it1)}")
                if(it1 in subList[subList.lastIndex]){
                    subResult.add(subList[subList.lastIndex].indexOf(it1))
                }
                else{
                    Log.e("PolicyBottomSheet", "에러 : 존재하지 않거나 다른지역의 태그")
                }
            }
            subSelectResult.add(subResult)
        }
        else{
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
        var mainTagSelectedList: List<Int>,
        val subTagSelectedList: List<List<Int>>,
        val subTagList: List<List<String>>
    )


}