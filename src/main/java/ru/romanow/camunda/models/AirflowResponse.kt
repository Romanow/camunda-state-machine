package ru.romanow.camunda.models

import java.util.*

data class AirflowResponse(
    var solveId: UUID? = null,
    var status: String? = null,
    val aggReportTableName: String? = null,
    val calculationParametersTables: Map<String, String>? = null,
)
