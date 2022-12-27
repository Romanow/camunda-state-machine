package ru.romanow.camunda.models

import ru.romanow.camunda.domain.enums.CalculationType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column

data class CalculationResponse(
    val uid: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val type: CalculationType? = null,
    val startDate: LocalDate? = null,
    val status: String? = null,
    val periods: List<PeriodResponse>? = null,
    val productScenarioUid: UUID? = null,
    val transferRateUid: UUID? = null,
    val macroUid: UUID? = null,
    val createdDate: LocalDateTime? = null,
    val modifiedDate: LocalDateTime? = null,
)