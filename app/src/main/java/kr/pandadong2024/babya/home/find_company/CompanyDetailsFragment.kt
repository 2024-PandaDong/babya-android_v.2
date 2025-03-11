package kr.pandadong2024.babya.home.find_company

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.databinding.FragmentCompanyDetailsBinding
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.DAO.TokenDAO
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.company.CompanyResponses
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast
import java.util.ArrayList

@AndroidEntryPoint
class CompanyDetailsFragment : Fragment() {

    private var _binding: FragmentCompanyDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : FindCompanyViewModel by viewModels()
    private var isExpanded = false
    var companyLink: String = ""
    private var address = ""
    private lateinit var token : String

    private val tag = "CompanyDetailsFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        lifecycleScope.launch {
            token = viewModel.getToken()?.accessToken.toString()
        }

        initView()

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.linkBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(companyLink))
            try {
                startActivity(intent)
            }
            catch (e : Exception){
                requireContext().shortToast("플레이 스토어에 승인을 받아주십시오")
            }
        }

        binding.editExplanation.setOnClickListener {
            if (isExpanded) {
                // 축약된 상태로 변경
                binding.explanation.maxLines = 2
                binding.explanation.ellipsize = TextUtils.TruncateAt.END
                binding.editExplanation.text = "더보기"
            } else {
                // 전체 텍스트를 보여주는 상태로 변경
                binding.explanation.maxLines = Int.MAX_VALUE
                binding.explanation.ellipsize = null
                binding.editExplanation.text = "닫기"
            }
            // 상태 변경
            isExpanded = !isExpanded
        }

        binding.locationCopy.setOnClickListener{
            copyAddressToClipboard(requireContext(), address)
        }

        binding.locationBtn.setOnClickListener {
            copyAddressToClipboard(requireContext(), address)
        }



        return binding.root
    }

    private fun initView() {
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                RetrofitBuilder.getCompanyService().getCompany(
                    accessToken = "Bearer ${token}",
                    id = viewModel.id.value!!
                )
            }.onSuccess { result ->
                setCompany(result)
            }
        }

    }

    private fun people(male: Int?, female: Int?) {

        val entire = (male ?: 0) + (female ?: 0)

        // 남성 비율 (전체 인원이 0일 경우 분모가 0이 되는 상황을 방지)
        val maleRatio = if (entire > 0) (male ?: 0).toFloat() / entire * 100 else 0f
        // 여성 비율
        val femaleRatio = if (entire > 0) (female ?: 0).toFloat() / entire * 100 else 0f

        val mpPieChart: PieChart = binding.Chart

        Log.d(tag, "setCompany: ${male?.toFloat()} ${female?.toFloat()}")

        // 전체 인원 표시
        binding.entireCount.text = "$entire 명"
        // 남성 비율 표시
        binding.maleCount.text = String.format("%.2f%%", maleRatio)
        // 여성 비율 표시
        binding.femaleCount.text = String.format("%.2f%%", femaleRatio)

        Log.d(tag, "setCompany: ${maleRatio} ${femaleRatio}")

        // 그래프에 나타낼 데이터
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(male?.toFloat() ?: 0f))
        entries.add(PieEntry(female?.toFloat()?: 0f))
        // 그래프 색상(데이터 순서)
        val colors = listOf(
            Color.parseColor("#69AEE3"),
            Color.parseColor("#FFA7A6")
        )

        // 데이터, 색상, 글자크기 및 색상 설정
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 0f

        // Pie 그래프 생성
        val dataMPchart = PieData(dataSet)
        mpPieChart.data = dataMPchart
        // 범례와 그래프 설명 비활성화
        mpPieChart.legend.isEnabled = false
        mpPieChart.description.isEnabled = false
        // 그래프 업데이트
        mpPieChart.invalidate()
    }


    private fun setCompany(result: BaseResponse<CompanyResponses>) {
        lifecycleScope.launch(Dispatchers.Main){
            binding.companyName.text = result.data?.name
            binding.mainImage.load(result.data?.contentImg)
            binding.explanation.text = result.data?.description
            companyLink = result.data?.link.toString()
            binding.field.text = result.data?.businessType+" | "
            binding.region.text = result.data?.address?.substringBefore(" ")
            address = result.data?.address.toString()
            binding.standard.text = result.data?.salaryYear.toString()+"년"

            binding.salaryMin.text = result.data?.minSalary.toString() + " 만원"
            binding.salaryAvg.text = result.data?.avgSalary.toString() + " 만원"
            binding.salaryMax.text = result.data?.maxSalary.toString() + " 만원"

            if (result.data?.malePeople == 0 && result.data?.femalePeople == 0) {
                binding.personnelLv.visibility = View.GONE
            }

            Log.d(tag, "setCompany: ${result.data?.malePeople} ${result.data?.femalePeople}")
            people(result.data?.malePeople, result.data?.femalePeople)

            binding.ceo.text = result.data?.ceo
            binding.customerService.text = result.data?.tel
            binding.location.text = result.data?.address
            binding.history.text = result.data?.historyYear
            binding.content.text = result.data?.businessContent
            binding.type.text = result.data?.companyType

            Log.d(TAG, "setCompany: ${result.data?.description}")

            // 해택 및 복지 추가
            binding.mtrLvPeriod.text = result.data?.mtrLvPeriod.toString() // 육아 휴직 기간
            binding.mtrLvSalary.text = result.data?.mtrLvSalary.toString() // 육아 휴직 급여 비율
             // 유급 여부
            if (result.data?.mtrLvIsSalary == "N") {
                binding.mtrLvIsSalary.text = "X"
            } else {
                binding.mtrLvIsSalary.text = "O"
            }
            // 재택근무 가능 여부
            if (result.data?.telComIsCan == "N"){
                binding.telComIsCan.text = "X"
            } else {
                binding.telComIsCan.text = "O"
            }
            binding.mtrSupMoney.text = result.data?.mtrSupMoney.toString() // 출산 비용 지원금
            binding.mtrSupCondition.text = result.data?.mtrSupCondition    // 출산 비용 지원 조건
            binding.telComDays.text = result.data?.telComDays.toString()   // 재택근무 일 수
            binding.telComTime.text = result.data?.telComTime.toString()   // 재택근무 시간
            binding.subsdType.text = result.data?.subsdType                // 보조금 지원 종류
            binding.subsdMoney.text = result.data?.subsdMoney.toString()   // 보조금 지원 액
        }
    }

    fun copyAddressToClipboard(context: Context, address: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("address", address)
        clipboard.setPrimaryClip(clip)

        // 선택사항: 복사 완료 메시지 표시
        Toast.makeText(context, "주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }
}