package ru.practicum.android.diploma.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.IndustryInteractor
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.IndustrySearchError

class IndustryViewModel(private val industryInteractor: IndustryInteractor) : ViewModel() {
    private val industries = mutableListOf<Industry>()
    private var selectedIndustryId: String? = null
    private val stateLiveData = MutableLiveData<IndustryState>(IndustryState.Empty)
    fun observeState(): LiveData<IndustryState> = stateLiveData

    fun loadIndustries() {
        viewModelScope.launch {
            stateLiveData.value = IndustryState.Loading
            val response = industryInteractor.getIndustries()
            try {
                if (!response.data.isNullOrEmpty()) {
                    industries.addAll(response.data)
                    stateLiveData.value = IndustryState.Content(industries)
                } else {
                    stateLiveData.value = IndustryState.Error(response.error ?: IndustrySearchError.Server)
                }

            } catch (_: Exception) {
                stateLiveData.value = IndustryState.Error(response.error ?: IndustrySearchError.Network)
            }
        }
    }
}
