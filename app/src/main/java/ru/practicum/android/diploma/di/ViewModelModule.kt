package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorites.FavoritesViewModel
import ru.practicum.android.diploma.ui.filter.FilterViewModel
import ru.practicum.android.diploma.ui.filter.IndustryViewModel
import ru.practicum.android.diploma.ui.search.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }
    viewModel { (id: String) ->
        VacancyViewModel(id, get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        IndustryViewModel(get(), get())
    }

    viewModel {
        FilterViewModel(get())
    }
}
