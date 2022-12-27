package ru.romanow.camunda.models

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PeriodRequest(
    @field:NotNull
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    var startDate: LocalDate? = null,
    @field:NotNull
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    var endDate: LocalDate? = null,
    var serialNumber: Int? = null,
    @field:NotEmpty
    var mark: String? = null,
)
