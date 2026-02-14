package ru.practicum.android.diploma.data.filter

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(
    private val prefs: SharedPreferences,
    private val gson: Gson
) {
    fun saveFilters(filters: FilterParameters) {
        prefs.edit()
            .putString(KEY_FILTERS, gson.toJson(filters))
            .apply()
    }

    fun getFilters(): FilterParameters {
        val json = prefs.getString(KEY_FILTERS, null)
            ?: return FilterParameters()

        return runCatching {
            gson.fromJson(json, FilterParameters::class.java)
        }.getOrElse {
            FilterParameters()
        }
    }

    fun clearFilters() {
        prefs.edit()
            .remove(KEY_FILTERS)
            .apply()
    }

    companion object {
        const val FILTER_PREFS_NAME = "filter_settings"
        private const val KEY_FILTERS = "filters_json"
    }
}
