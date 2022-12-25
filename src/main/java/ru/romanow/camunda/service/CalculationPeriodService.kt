package ru.romanow.camunda.service

import ru.romanow.camunda.domain.Calculation
import ru.romanow.camunda.models.PeriodResponse
import ru.romanow.camunda.models.PeriodRequest
import java.util.*

interface CalculationPeriodService {
    fun findByCalculationUid(calculationUid: UUID): List<PeriodResponse>
    fun create(periods: List<PeriodRequest>, calculation: Calculation)
}