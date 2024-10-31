package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyTextBottomSheetBinding
import kr.pandadong2024.babya.databinding.FragmentSignupBottomSheetBinding

class PolicyTextBottomSheet(
    val type : Policy
) : BottomSheetDialogFragment() {
    var url = ""

    private var _binding: FragmentPolicyTextBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPolicyTextBottomSheetBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: ${type}")

        val vebView = binding.privacyText

        binding.privacyText.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        when (type.name ){
            Policy.PRIVACY.name -> url = "https://synonymous-foundation-c30.notion.site/1169bbf0448c80e1accdc3511654fe06"
            Policy.SERVICE.name -> url = "https://synonymous-foundation-c30.notion.site/1279bbf0448c80eeba5fe9cfbca9b6d8"
            Policy.INFORMATION.name -> url = "https://synonymous-foundation-c30.notion.site/3-1279bbf0448c80be9767c7a189419f7b"
        }

        vebView.loadUrl(url)
        Log.d(TAG, "onCreateView: ${url}")






        binding.checkBtn.setOnClickListener {
            url = ""
            this.dismiss()
        }



        return binding.root
    }
}

enum class Policy{
    PRIVACY,
    SERVICE,
    INFORMATION
}