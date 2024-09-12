package kr.pandadong2024.babya.home.find_company.find_company_viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FindCompanyViewModel: ViewModel() {
    val id = MutableLiveData<Int>().apply { value = -1 }
}