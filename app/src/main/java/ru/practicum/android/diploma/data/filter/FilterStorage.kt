package ru.practicum.android.diploma.data.filter

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(FILTER_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveFilters(filters: FilterParameters) {
        prefs.edit()
            .putString(KEY_FILTERS, gson.toJson(filters))
            .apply()
    }

    fun getFilters(): FilterParameters {
        val json = prefs.getString(KEY_FILTERS, null)
        return if (json != null) {
            gson.fromJson(json, FilterParameters::class.java)
        } else {
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
