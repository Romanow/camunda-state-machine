package ru.romanow.camunda.models

import ru.romanow.camunda.domain.CalculationStatus
import ru.romanow.camunda.domain.enums.CalculationType
import java.time.LocalDateTime
import java.util.*

data class CalculationResponse(
    val uid: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val type: CalculationType? = null,
    val status: CalculationStatusResponse? = null,
    val periods: List<PeriodResponse>? = null,
    val createdDate: LocalDateTime? = null,
    val modifiedDate: LocalDateTime? = null,
)