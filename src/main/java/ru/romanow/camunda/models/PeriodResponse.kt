package ru.romanow.camunda.models

import java.time.LocalDate
import java.util.*

data class PeriodResponse(
    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    var serialNumber: Int? = null,
    var mark: String? = null,
)