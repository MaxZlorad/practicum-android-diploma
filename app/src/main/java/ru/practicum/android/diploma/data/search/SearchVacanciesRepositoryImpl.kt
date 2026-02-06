package ru.practicum.android.diploma.data.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.data.dto.VacancyRequest
import ru.practicum.android.diploma.data.dto.VacancyResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.NetworkCodes
import ru.practicum.android.diploma.domain.api.FilterRepository
import ru.practicum.android.diploma.domain.api.SearchVacanciesRepository
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.domain.models.VacancySearchFilter
import ru.practicum.android.diploma.domain.models.VacancySearchResult

class SearchVacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
    private val filterRepository: FilterRepository
) : SearchVacanciesRepository {

    override fun searchVacancies(filter: VacancySearchFilter): Flow<VacancySearchResult> = flow {
        val savedFilters = filterRepository.getFilters()
        val queryMap = mutableMapOf<String, String>().apply {
            put("text", filter.text ?: "")
            put("page", filter.page.toString())

            savedFilters.industryId?.let { put("industry", it.toString()) }

            savedFilters.salaryFrom?.let { put("salary", it.toString()) }

            if (savedFilters.onlyWithSalary) { put("only_with_salary", "true") }
        }
        val response = networkClient.doRequest(VacancyRequest(queryMap))
        when (response.resultCode) {
            NetworkCodes.SUCCESS_CODE -> {
                val vacanciesResponse = response as VacancyResponse
                val vacancies: List<Vacancy> =
                    VacancyDtoMapper.mapList(vacanciesResponse.vacancies)

                emit(
                    VacancySearchResult(
                        totalFound = vacanciesResponse.found,
                        totalPages = vacanciesResponse.pages,
                        vacancies = vacancies,
                        errorCode = NetworkCodes.SUCCESS_CODE
                    )
                )
            }

            else -> {
                emit(
                    VacancySearchResult(
                        totalFound = 0,
                        totalPages = 0,
                        vacancies = emptyList(),
                        errorCode = response.resultCode
                    )
                )
            }
        }
    }.flowOn(Dispatchers.IO)
}
