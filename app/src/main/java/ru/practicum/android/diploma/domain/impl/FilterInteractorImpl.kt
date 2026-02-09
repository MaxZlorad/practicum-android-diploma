package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.api.FilterInteractor
import ru.practicum.android.diploma.domain.api.FilterRepository
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterInteractorImpl(
    private val repository: FilterRepository
) : FilterInteractor {

    override fun getFilters(): FilterParameters =
        repository.getFilters()

    override fun saveFilters(filters: FilterParameters) =
        repository.saveFilters(filters)

    override fun clearFilters() =
        repository.clearFilters()
}
