package ru.practicum.android.diploma.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.FilterInteractor
import ru.practicum.android.diploma.domain.api.IndustryInteractor
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.Industry
import java.util.Locale

class IndustryViewModel(
    private val industryInteractor: IndustryInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {
    private val allIndustries = mutableListOf<Industry>()
    private var selectedIndustryId: Int? = null
    private var savedIndustryId: Int? = null
    private val _stateLiveData = MutableLiveData<IndustryState>()
    val stateLiveData: LiveData<IndustryState> = _stateLiveData
    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled
    private var selectedIndustryName: String? = null
    fun loadIndustries() {
        _stateLiveData.value = IndustryState.Loading

        viewModelScope.launch {
            allIndustries.clear()

            val response = industryInteractor.getIndustries()
            val data = response.data
                ?.sortedBy { it.name.lowercase(Locale.getDefault()) }
                ?: emptyList()

            allIndustries.addAll(data)

            val savedFilters = filterInteractor.getFilters()
            savedIndustryId = savedFilters.industryId
            selectedIndustryId = savedIndustryId
            selectedIndustryName = savedFilters.industryName


            if (savedIndustryId != null) {
                selectedIndustryName = allIndustries.find { it.id == savedIndustryId }?.name
            }

            if (savedIndustryId != null &&
                allIndustries.none { it.id == savedIndustryId }
            ) {
                savedIndustryId = null
                selectedIndustryId = null
                selectedIndustryName = null
            }

            when {
                response.error != null ->
                    _stateLiveData.postValue(IndustryState.Error(response.error))

                data.isEmpty() ->
                    _stateLiveData.postValue(IndustryState.Empty)

                else ->
                    _stateLiveData.postValue(IndustryState.Content(data))
            }
            updateButtonEnabled()
        }
    }

    fun search(query: String) {
        val filtered = if (query.isBlank()) {
            allIndustries
        } else {
            allIndustries.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        _stateLiveData.value = if (filtered.isEmpty()) {
            IndustryState.Empty
        } else {
            IndustryState.Content(filtered)
        }
    }

    fun getSelectedIndustryId(): Int? = selectedIndustryId
    fun getSelectedIndustryName(): String? = selectedIndustryName

    fun onIndustrySelected(industryId: Int) {
        selectedIndustryId = industryId
        selectedIndustryName = allIndustries.find { it.id == industryId }?.name
        updateButtonEnabled()

        (_stateLiveData.value as? IndustryState.Content)?.let {
            _stateLiveData.value = it.copy(industries = it.industries)
        }
    }

    private fun updateButtonEnabled() {
        _isButtonEnabled.value =
            selectedIndustryId != null || savedIndustryId != null
    }

    fun onApplyClicked() {
        val industryToSave = selectedIndustryId ?: savedIndustryId ?: return
        val industryNameToSave = selectedIndustryName ?: allIndustries.find { it.id == industryToSave }?.name
        val currentFilters = filterInteractor.getFilters()
        val updatedFilters = FilterParameters(
            industryId = industryToSave,
            industryName = industryNameToSave,
            salaryFrom = currentFilters.salaryFrom,
            onlyWithSalary = currentFilters.onlyWithSalary
        )

        filterInteractor.saveFilters(updatedFilters)

        savedIndustryId = industryToSave
        selectedIndustryId = industryToSave
        selectedIndustryName = industryNameToSave
        updateButtonEnabled()
    }
}
