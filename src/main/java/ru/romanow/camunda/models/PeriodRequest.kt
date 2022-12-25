package ru.romanow.camunda.models

import java.time.LocalDate
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PeriodRequest(
    @field:NotNull
    var startDate: LocalDate? = null,
    @field:NotNull
    var endDate: LocalDate? = null,
    @field:NotEmpty
    var mark: String? = null,
    var serialNumber: Int? = null,
)
