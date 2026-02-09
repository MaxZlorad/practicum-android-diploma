package ru.practicum.android.diploma.ui.filter

import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.IndustrySearchError

sealed class IndustryState {
    object Loading : IndustryState()
    object Empty : IndustryState()
    data class Error(val error: IndustrySearchError) : IndustryState()
    data class Content(val industries: List<Industry>) : IndustryState()
}
