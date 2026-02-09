package ru.practicum.android.diploma.data.search

import android.content.res.Resources
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
    private val filterRepository: FilterRepository,
    private val resources: Resources
) : SearchVacanciesRepository {

    override fun searchVacancies(filter: VacancySearchFilter): Flow<VacancySearchResult> = flow {
        val queryMap = buildQueryMap(filter)
        emit(executeSearch(queryMap))
    }.flowOn(Dispatchers.IO)

    private fun buildQueryMap(filter: VacancySearchFilter): Map<String, String> {
        val savedFilters = filterRepository.getFilters()
        return mutableMapOf<String, String>().apply {
            put(KEY_TEXT, filter.text ?: EMPTY_STRING)
            put(KEY_PAGE, filter.page.toString())

            savedFilters.industryId?.let {
                put(KEY_INDUSTRY, it.toString())
            }

            savedFilters.salaryFrom?.let {
                put(KEY_SALARY, it.toString())
            }

            if (savedFilters.onlyWithSalary) {
                put(KEY_ONLY_WITH_SALARY, VALUE_TRUE)
            }
        }
    }

    private suspend fun executeSearch(queryMap: Map<String, String>): VacancySearchResult {
        val response = networkClient.doRequest(VacancyRequest(queryMap))
        return when (response.resultCode) {
            NetworkCodes.SUCCESS_CODE -> {
                val vacanciesResponse = response as VacancyResponse
                val vacancies: List<Vacancy> =
                    VacancyDtoMapper.mapList(
                        vacanciesResponse.vacancies,
                        resources
                    )
                VacancySearchResult(
                    totalFound = vacanciesResponse.found,
                    totalPages = vacanciesResponse.pages,
                    vacancies = vacancies,
                    errorCode = NetworkCodes.SUCCESS_CODE
                )
            }

            else -> {
                VacancySearchResult(
                    totalFound = 0,
                    totalPages = 0,
                    vacancies = emptyList(),
                    errorCode = response.resultCode
                )
            }
        }
    }

    companion object {
        private const val KEY_TEXT = "text"
        private const val KEY_PAGE = "page"
        private const val KEY_INDUSTRY = "industry"
        private const val KEY_SALARY = "salary"
        private const val KEY_ONLY_WITH_SALARY = "only_with_salary"
        private const val VALUE_TRUE = "true"
        private const val EMPTY_STRING = ""
    }
}
