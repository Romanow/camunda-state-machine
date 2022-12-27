package ru.romanow.camunda.models

import java.util.*

data class StartCalculationCommand(
    var solveId: UUID? = null,
    var calculationParametersTables: CalculationParametersTables? = null
)
