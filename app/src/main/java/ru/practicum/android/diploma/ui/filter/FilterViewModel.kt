// Файл: ru.practicum.android.diploma.ui.filter.FilterViewModel
package ru.practicum.android.diploma.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.api.FilterInteractor
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterViewModel(
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val _filterState = MutableLiveData<FilterState>()
    val filterState: LiveData<FilterState> = _filterState

    private val _industryName = MutableLiveData<String?>()
    val industryName: LiveData<String?> = _industryName

    init {
        loadSavedFilters()
    }

    private fun loadSavedFilters() {
        val savedFilters = filterInteractor.getFilters()

        _filterState.value = FilterState(
            salaryFrom = savedFilters.salaryFrom,
            onlyWithSalary = savedFilters.onlyWithSalary,
            industryId = savedFilters.industryId
        )

        // ДОБАВЛЕНО: Загружаем имя отрасли из сохраненных фильтров
        _industryName.value = savedFilters.industryName
    }

    fun updateSalary(salary: Int?) {
        val currentState = _filterState.value ?: FilterState()
        val currentIndustryName = _industryName.value

        val updatedFilters = FilterParameters(
            industryId = currentState.industryId,
            industryName = currentIndustryName, // Сохраняем текущее имя
            salaryFrom = salary,
            onlyWithSalary = currentState.onlyWithSalary
        )

        filterInteractor.saveFilters(updatedFilters)

        _filterState.value = currentState.copy(salaryFrom = salary)
    }

    fun updateOnlyWithSalary(onlyWithSalary: Boolean) {
        val currentState = _filterState.value ?: FilterState()
        val currentIndustryName = _industryName.value

        val updatedFilters = FilterParameters(
            industryId = currentState.industryId,
            industryName = currentIndustryName, // Сохраняем текущее имя
            salaryFrom = currentState.salaryFrom,
            onlyWithSalary = onlyWithSalary
        )

        filterInteractor.saveFilters(updatedFilters)

        _filterState.value = currentState.copy(onlyWithSalary = onlyWithSalary)
    }

    fun updateIndustry(industryId: Int?, industryName: String?) {
        val currentState = _filterState.value ?: FilterState()

        val updatedFilters = FilterParameters(
            industryId = industryId,
            industryName = industryName, // Сохраняем переданное имя
            salaryFrom = currentState.salaryFrom,
            onlyWithSalary = currentState.onlyWithSalary
        )

        filterInteractor.saveFilters(updatedFilters)

        _filterState.value = currentState.copy(industryId = industryId)
        _industryName.value = industryName
    }

    fun resetFilters() {
        filterInteractor.clearFilters()

        _filterState.value = FilterState()
        _industryName.value = null
    }
}

data class FilterState(
    val salaryFrom: Int? = null,
    val onlyWithSalary: Boolean = false,
    val industryId: Int? = null
)
