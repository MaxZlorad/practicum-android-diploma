package ru.practicum.android.diploma.data.filter

import android.content.Context
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(
    context: Context,
    private val gson: Gson
) {
    private val prefs =
        context.getSharedPreferences(FILTER_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveFilters(filters: FilterParameters) {
        prefs.edit()
            .putString(KEY_FILTERS, gson.toJson(filters))
            .apply()
    }

    fun getFilters(): FilterParameters {
        val json = prefs.getString(KEY_FILTERS, null) ?: return FilterParameters()
        return runCatching {
            gson.fromJson(json, FilterParameters::class.java)
        }.getOrElse {
            FilterParameters()
        }
    }

    fun clearFilters() {
        prefs.edit().remove(KEY_FILTERS).apply()
    }

    companion object {
        private const val FILTER_PREFS_NAME = "filter_settings"
        private const val KEY_FILTERS = "filters_json"
    }
}
