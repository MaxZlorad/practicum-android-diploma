package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.api.SearchVacanciesRepository
import ru.practicum.android.diploma.domain.models.VacancySearchFilter
import ru.practicum.android.diploma.domain.models.VacancySearchResult

class SearchVacanciesInteractorImpl(private val repository: SearchVacanciesRepository) :
    SearchVacanciesInteractor {
    override fun searchVacancies(filters: VacancySearchFilter): Flow<VacancySearchResult> {
        val sanitizedFilters = filters.copy(
            text = filters.text?.let { sanitizeText(it) }
        )
        return repository.searchVacancies(sanitizedFilters)
    }

    /**
     * Легкая очистка на клиентской стороне:
     * - Удаляем управляющие и необычные символы
     * - Сводим несколько пробелов к одному
     *
     * ВНИМАНИЕ: это "не полная очистка". На сервере выполняется
     * полноценная нормализация, фильтрация и проверка безопасности.
     */
    private fun sanitizeText(text: String): String {
        return text
            // Оставляем буквы, цифры, базовую пунктуацию и пробелы
            .replace(Regex("[^\\p{L}\\p{N}.&\\-'/ ]+"), "")
            // Сводим несколько пробелов к одному
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
