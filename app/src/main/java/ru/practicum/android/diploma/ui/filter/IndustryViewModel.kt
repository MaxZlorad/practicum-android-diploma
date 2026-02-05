package ru.practicum.android.diploma.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.IndustryInteractor
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.IndustrySearchError
import java.util.Locale

class IndustryViewModel(
    private val industryInteractor: IndustryInteractor,
    // provate val sharedPrefInteractor: SharedPrefInteractor
) : ViewModel() {
    private val industries = mutableListOf<Industry>()
    private var selectedIndustryId: String? = null
    private var savedIndustryId: Int? = NO_SELECTED
    private val _stateLiveData = MutableLiveData<IndustryState>(IndustryState.Empty)
    val stateLiveData: LiveData<IndustryState> = _stateLiveData
    private val _isButtonVisibleLiveData = MutableLiveData<Boolean>(false)
    val isButtonVisibleLiveData: LiveData<Boolean> = _isButtonVisibleLiveData


    fun observeIsButtonVisible(): LiveData<Boolean> = isButtonVisibleLiveData

    fun observeState(): LiveData<IndustryState> = stateLiveData

    fun loadIndustries() {
        _stateLiveData.value = IndustryState.Loading
        viewModelScope.launch {
            // savedIndustryId = sharedPrefInteractor.getFilter().industryId?: NO_SELECTED
            industries.clear()
            val response = industryInteractor.getIndustries()
            if (!response.data.isNullOrEmpty()) {
                industries.addAll(response.data)
            }
            processResult(industries, response.error)
        }
    }


    private fun processResult(foundIndustries: List<Industry>?, errorCode: IndustrySearchError?) {
        industries.clear()
        if (foundIndustries != null) {
            val sortedIndustries = foundIndustries.sortedBy {
                it.name.lowercase(Locale.getDefault())
            }
            industries.addAll(sortedIndustries)
        }
        when {
            errorCode != null -> {
                renderState(IndustryState.Error(errorCode))
            }

            industries.isEmpty() -> {
                renderState(IndustryState.Empty)
            }

            else -> {
                renderState(IndustryState.Content(industries))
            }
        }
        // updateButtonVisibility()
    }

    private fun renderState(state: IndustryState) {
        _stateLiveData.postValue(state)
    }

    /*    private fun updateButtonVisibility() {
            val isSelectedInCurrentList = selectedIndustryId?.let { selectedId ->
                filteredIndustries.any { it.id == selectedId }
            } ?: false
            if (selectedIndustryId == null || !isSelectedInCurrentList) {
                isButtonVisibleLiveData.postValue(false)
                return
            }
            val shouldShow = savedIndustryId == null || selectedIndustryId != savedIndustryId
            isButtonVisibleLiveData.postValue(shouldShow)
        }*/

    companion object {
        private const val NO_SELECTED = -1
        private const val HARDCODED_TEST = 7 // for test without shared prefs interactor
    }
}
