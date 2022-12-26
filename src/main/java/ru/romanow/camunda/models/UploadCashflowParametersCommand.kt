package ru.romanow.camunda.models

import java.util.*

data class UploadCashflowParametersCommand(
    var solveId: UUID? = null,
    var calculationSourceTables: List<String>? = null,
)