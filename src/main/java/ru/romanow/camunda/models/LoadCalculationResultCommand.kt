package ru.romanow.camunda.models

import java.util.*

data class LoadCalculationResultCommand(
    var solveId: UUID? = null,
    var aggregationReportTableName: String? = null,
    var targetReportTableName: String? = null
)
