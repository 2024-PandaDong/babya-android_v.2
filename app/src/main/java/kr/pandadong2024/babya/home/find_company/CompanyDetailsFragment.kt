package kr.pandadong2024.babya.home.find_company

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kr.pandadong2024.babya.databinding.FragmentCompanyDetailsBinding
import kr.pandadong2024.babya.util.BottomControllable
import java.util.ArrayList

class CompanyDetailsFragment : Fragment() {

    private var _binding: FragmentCompanyDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)

        // 남성 비율
        val maleRatio = 530f
        // 여성 비율
        val femaleRatio = 150f

        val mpPieChart : PieChart = binding.Chart

        // 그래프에 나타낼 데이터
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(femaleRatio))
        entries.add(PieEntry(maleRatio))
        // 그래프 색상(데이터 순서)
        val colors = listOf(
            Color.parseColor("#FFA7A6"),
            Color.parseColor("#69AEE3")
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

        return binding.root
    }



}