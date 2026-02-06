package ru.practicum.android.diploma.domain.models

data class FilterParameters(
    val industryId: Int? = null,
    val salaryFrom: Int? = null,
    val onlyWithSalary: Boolean = false
)
