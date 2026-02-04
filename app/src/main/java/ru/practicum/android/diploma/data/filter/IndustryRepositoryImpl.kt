package ru.practicum.android.diploma.data.filter

import ru.practicum.android.diploma.data.dto.IndustryRequest
import ru.practicum.android.diploma.data.dto.IndustryResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.NetworkCodes
import ru.practicum.android.diploma.domain.api.IndustryRepository
import ru.practicum.android.diploma.domain.models.IndustrySearchError
import ru.practicum.android.diploma.domain.models.IndustrySearchResult

class IndustryRepositoryImpl(private val networkClient: NetworkClient) : IndustryRepository {
    override suspend fun getIndustries(): IndustrySearchResult {
        return try {
            val response = networkClient.doRequest(IndustryRequest())
            when (response.resultCode) {
                NetworkCodes.SERVER_ERROR_CODE -> {
                    IndustrySearchResult(null, IndustrySearchError.Server)
                }

                NetworkCodes.SUCCESS_CODE -> {
                    val industryResponse = response as IndustryResponse
                    val data = industryResponse.industries
                    IndustrySearchResult(data, null)
                }

                else -> {
                    IndustrySearchResult(null, IndustrySearchError.Network)
                }
            }
        } catch (_: Exception) {
            IndustrySearchResult(null, IndustrySearchError.Network)
        }
    }
}
