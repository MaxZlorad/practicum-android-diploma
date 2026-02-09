package ru.practicum.android.diploma.domain.api

import ru.practicum.android.diploma.domain.models.FilterParameters

interface FilterInteractor {
    fun getFilters(): FilterParameters
    fun saveFilters(filters: FilterParameters)
    fun clearFilters()
}
