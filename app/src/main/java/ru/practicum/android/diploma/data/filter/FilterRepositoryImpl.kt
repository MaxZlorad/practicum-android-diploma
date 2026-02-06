package ru.practicum.android.diploma.data.filter

import ru.practicum.android.diploma.domain.api.FilterRepository
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterRepositoryImpl(private val storage: FilterStorage) : FilterRepository {
    override fun getFilters(): FilterParameters = storage.getFilters()
    override fun saveFilters(filters: FilterParameters) = storage.saveFilters(filters)
    override fun clearFilters() = storage.clearFilters()
}
