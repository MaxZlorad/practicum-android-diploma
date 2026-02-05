package ru.practicum.android.diploma.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.IndustryInteractor
import ru.practicum.android.diploma.domain.models.Industry
import java.util.Locale

class IndustryViewModel(
    private val industryInteractor: IndustryInteractor,
    // private val sharedPrefInteractor: SharedPrefInteractor
) : ViewModel() {
    private val allIndustries = mutableListOf<Industry>()
    private var selectedIndustryId: Int = NOT_SELECTED
    private var savedIndustryId: Int = NOT_SELECTED
    private val _stateLiveData = MutableLiveData<IndustryState>()
    val stateLiveData: LiveData<IndustryState> = _stateLiveData
    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled
    fun loadIndustries() {
        _stateLiveData.value = IndustryState.Loading

        viewModelScope.launch {
            allIndustries.clear()

            val response = industryInteractor.getIndustries()
            val data = response.data
                ?.sortedBy { it.name.lowercase(Locale.getDefault()) }
                ?: emptyList()

            allIndustries.addAll(data)

            // TEMP:
            // savedIndustryId = sharedPrefInteractor.getFilter().industryId ?: NOT_SELECTED
            savedIndustryId = HARDCODED_TEST
            selectedIndustryId = savedIndustryId

            if (savedIndustryId != NOT_SELECTED &&
                allIndustries.none { it.id == savedIndustryId }
            ) {
                savedIndustryId = NOT_SELECTED
                selectedIndustryId = NOT_SELECTED
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

        if (filtered.isEmpty()) {
            _stateLiveData.value = IndustryState.Empty
        } else {
            _stateLiveData.value = IndustryState.Content(filtered)
        }
    }

    fun getSelectedIndustryId(): Int? =
        selectedIndustryId.takeIf { it != NOT_SELECTED }

    fun onIndustrySelected(industryId: Int) {
        selectedIndustryId = industryId
        updateButtonEnabled()

        (_stateLiveData.value as? IndustryState.Content)?.let {
            _stateLiveData.value = it.copy(industries = it.industries)
        }
    }

    private fun updateButtonEnabled() {
        _isButtonEnabled.value =
            savedIndustryId != NOT_SELECTED ||
                selectedIndustryId != NOT_SELECTED
    }

    fun onApplyClicked() {
        val industryToSave =
            if (selectedIndustryId != NOT_SELECTED)
                selectedIndustryId
            else
                savedIndustryId

        if (industryToSave == NOT_SELECTED) return

        // sharedPrefInteractor.saveFilter(
        //     Filter(industryId = industryToSave)
        // )

        savedIndustryId = industryToSave
        selectedIndustryId = industryToSave
        updateButtonEnabled()
    }
    companion object {
        private const val NOT_SELECTED = -1
        private const val HARDCODED_TEST = 7 // for test without shared prefs interactor
    }
}
