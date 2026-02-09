package ru.practicum.android.diploma.domain.models

sealed class IndustrySearchError {
    object Network : IndustrySearchError()
    object Server : IndustrySearchError()
}
