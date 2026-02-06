package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.filter.FilterRepositoryImpl
import ru.practicum.android.diploma.data.filter.FilterStorage
import ru.practicum.android.diploma.data.filter.IndustryRepositoryImpl
import ru.practicum.android.diploma.data.search.SearchVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.vacancy.FavoritesRepositoryImpl
import ru.practicum.android.diploma.data.vacancy.VacancyDetailsRepositoryImpl
import ru.practicum.android.diploma.domain.api.FavoritesRepository
import ru.practicum.android.diploma.domain.api.FilterRepository
import ru.practicum.android.diploma.domain.api.IndustryRepository
import ru.practicum.android.diploma.domain.api.SearchVacanciesRepository
import ru.practicum.android.diploma.domain.api.VacancyDetailsRepository

val repositoryModule = module {
    single<SearchVacanciesRepository> {
        SearchVacanciesRepositoryImpl(get(), get<FilterRepository>(), androidContext().resources)
    }
    single<VacancyDetailsRepository> {
        VacancyDetailsRepositoryImpl(get(), get(), androidContext())
    }
    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get())
    }
    single<IndustryRepository> {
        IndustryRepositoryImpl(get())
    }
    single<FilterRepository> {
        FilterRepositoryImpl(get())
    }
    single { FilterStorage(androidContext(), get()) }
}
