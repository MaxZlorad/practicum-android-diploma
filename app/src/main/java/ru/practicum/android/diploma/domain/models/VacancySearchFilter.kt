package ru.practicum.android.diploma.domain.models

data class VacancySearchFilter(
    val page: Int = 1,
    val text: String? = null,
    val industryId: Int? = null,
    val salaryFrom: Int? = null,
    val onlyWithSalary: Boolean = false
)
