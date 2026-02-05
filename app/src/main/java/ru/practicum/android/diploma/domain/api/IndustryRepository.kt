package ru.practicum.android.diploma.domain.api

import ru.practicum.android.diploma.domain.models.IndustrySearchResult

interface IndustryRepository {
    suspend fun getIndustries(): IndustrySearchResult
}
