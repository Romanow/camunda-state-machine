package ru.romanow.camunda.models

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.util.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.PastOrPresent

data class CreateCalculationRequest(
    @field:NotEmpty
    val name: String? = null,
    val description: String? = null,
    @field:NotEmpty
    val periods: List<PeriodRequest>? = null,
    @field:NotNull
    @field:PastOrPresent
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    val startDate: LocalDate? = null,
    @field:NotNull
    val macroUid: UUID? = null,
    @field:NotNull
    val transferRateUid: UUID? = null,
    @field:NotNull
    val productScenarioUid: UUID? = null,
)
