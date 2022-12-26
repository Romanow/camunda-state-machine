package ru.romanow.camunda.models

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class PeriodResponse(
    @JsonFormat(pattern = "dd-MM-yyyy")
    var startDate: LocalDate? = null,
    @JsonFormat(pattern = "dd-MM-yyyy")
    var endDate: LocalDate? = null,
    var serialNumber: Int? = null,
    var mark: String? = null,
)