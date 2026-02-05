package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.api.IndustryInteractor
import ru.practicum.android.diploma.domain.api.IndustryRepository
import ru.practicum.android.diploma.domain.models.IndustrySearchResult

class IndustryInteractorImpl(private val repository: IndustryRepository) : IndustryInteractor {
    override suspend fun getIndustries(): IndustrySearchResult {
        return repository.getIndustries()
    }
}
