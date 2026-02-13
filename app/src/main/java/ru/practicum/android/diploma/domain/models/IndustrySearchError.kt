package ru.practicum.android.diploma.domain.models

sealed interface IndustrySearchError {
    object Network : IndustrySearchError
    object Server : IndustrySearchError
}
