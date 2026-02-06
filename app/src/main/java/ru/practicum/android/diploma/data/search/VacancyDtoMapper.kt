package ru.practicum.android.diploma.data.search

import android.content.res.Resources
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.PhoneDto
import ru.practicum.android.diploma.data.dto.Salary
import ru.practicum.android.diploma.data.dto.VacancyDto
import ru.practicum.android.diploma.domain.models.Vacancy
import java.util.Locale

object VacancyDtoMapper {
    fun map(dto: VacancyDto, resources: Resources): Vacancy {
        val displayName = formatName(dto.name, dto.address?.city, dto.area.name)

        return Vacancy(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            experience = dto.experience?.name,
            schedule = dto.schedule?.name,
            employment = dto.employment?.name,
            areaName = dto.area.name,
            industryName = dto.industry.name,
            skills = dto.skills,
            url = dto.url,
            salaryFrom = dto.salary?.from,
            salaryTo = dto.salary?.to,
            currency = dto.salary?.currency,
            salaryTitle = formatSalary(dto.salary, resources),
            city = dto.address?.city,
            street = dto.address?.street,
            building = dto.address?.building,
            fullAddress = dto.address?.raw,
            contactName = dto.contacts?.name,
            email = dto.contacts?.email,
            phones = formatPhones(dto.contacts?.phones),
            employerName = dto.employer.name,
            logoUrl = dto.employer.logo,
            displayName = displayName
        )
    }

    fun mapList(
        dtoList: List<VacancyDto>?,
        resources: Resources
    ): List<Vacancy> {
        return dtoList?.map { map(it, resources) } ?: emptyList()
    }

    fun formatSalary(
        salary: Salary?,
        resources: Resources
    ): String {
        if (salary == null) {
            return resources.getString(R.string.salary_not_specified)
        }

        val from = salary.from?.let { formatNumber(it) }
        val to = salary.to?.let { formatNumber(it) }

        val base = when {
            from != null && to != null ->
                resources.getString(R.string.salary_from_to, from, to)

            from != null ->
                resources.getString(R.string.salary_from, from)

            to != null ->
                resources.getString(R.string.salary_to, to)

            else ->
                resources.getString(R.string.salary_not_specified)
        }

        return salary.currency?.let { currency ->
            "$base ${getCurrencySymbol(currency)}"
        } ?: base
    }

    fun formatName(name: String, city: String?, areaName: String): String {
        return "$name, ${city ?: areaName}"
    }

    private fun formatNumber(number: Int): String {
        return String.Companion.format(Locale.getDefault(), "%,d", number)
            .replace(',', ' ')
    }

    private fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode.uppercase()) {
            "RUR", "RUB" -> "₽"
            "BYR" -> "Br"
            "USD" -> "$"
            "EUR" -> "€"
            "KZT" -> "₸"
            "UAH" -> "₴"
            "AZN" -> "₼"
            "UZS" -> "сўм"
            "GEL" -> "₾"
            "KGT" -> "сом"
            else -> currencyCode
        }
    }

    private fun formatPhones(phoneDtos: List<PhoneDto>?): List<String>? {
        return phoneDtos?.map { phoneDto ->
            if (phoneDto.comment != null && phoneDto.comment.isNotBlank()) {
                "${phoneDto.formatted} (${phoneDto.comment})"
            } else {
                phoneDto.formatted
            }
        }
    }
}
