package ru.practicum.android.diploma.domain.models

sealed class IndustrySearchError {
    object Network : IndustrySearchError()
    object NotFound : IndustrySearchError()
    object Server : IndustrySearchError()
}
