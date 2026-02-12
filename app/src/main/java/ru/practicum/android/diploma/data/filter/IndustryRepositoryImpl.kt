package ru.practicum.android.diploma.data.filter

import ru.practicum.android.diploma.data.dto.IndustryRequest
import ru.practicum.android.diploma.data.dto.IndustryResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.NetworkCodes
import ru.practicum.android.diploma.domain.api.IndustryRepository
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.IndustrySearchError
import ru.practicum.android.diploma.domain.models.IndustrySearchResult

class IndustryRepositoryImpl(private val networkClient: NetworkClient) : IndustryRepository {
    override suspend fun getIndustries(): IndustrySearchResult {
        val response = networkClient.doRequest(IndustryRequest())
        return when (response.resultCode) {
            NetworkCodes.SUCCESS_CODE -> {
                val industryResponse = response as IndustryResponse
                val data = industryResponse.industries.map {
                    Industry(it.id, it.name)
                }
                IndustrySearchResult(data, null)
            }

            NetworkCodes.SERVER_ERROR_CODE ->
                IndustrySearchResult(null, IndustrySearchError.Server)

            NetworkCodes.NO_NETWORK_CODE,
            NetworkCodes.TIMEOUT_CODE ->
                IndustrySearchResult(null, IndustrySearchError.Network)

            else ->
                IndustrySearchResult(null, IndustrySearchError.Network)
        }
    }
}
