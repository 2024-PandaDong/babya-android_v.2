package kr.pandadong2024.babya.home.map

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.databinding.FragmentMapBottomSheetBinding
import kr.pandadong2024.babya.util.shortToast


class MapBottomSheet(val placeData: MapData, onClick: () -> Unit) : BottomSheetDialogFragment() {
    private var _binding: FragmentMapBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val testViewModel by activityViewModels<MapViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBottomSheetBinding.inflate(inflater, container, false)

        binding.name.text = placeData.placeName
        binding.tel.text = placeData.tel
        binding.address.text = placeData.addressName
        binding.roadAddress.text = placeData.roadAddressName


        binding.addressBtn.setOnClickListener {
            copyAddressToClipboard(requireContext(), placeData.addressName)
        }
        binding.addressCopy.setOnClickListener {
            copyAddressToClipboard(requireContext(), placeData.addressName)
        }
        binding.roadAddressBtn.setOnClickListener{
            copyAddressToClipboard(requireContext(), placeData.roadAddressName.toString())
        }
        binding.roadAddressCopy.setOnClickListener{
            copyAddressToClipboard(requireContext(), placeData.roadAddressName.toString())
        }

        binding.linkBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placeData.placeUrl))
            try {
                startActivity(intent)
            }
            catch (e : Exception){
                requireContext().shortToast("플레이 스토어에 승인을 받아주십시오")
            }
        }


        return binding.root
    }


    fun copyAddressToClipboard(context: Context, address: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("address", address)
        clipboard.setPrimaryClip(clip)

        // 선택사항: 복사 완료 메시지 표시
        Toast.makeText(context, "주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

}