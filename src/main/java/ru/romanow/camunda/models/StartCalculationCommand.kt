package ru.romanow.camunda.models

import java.util.*

data class StartCalculationCommand(
    var solveId: UUID? = null,
    var settings: StartCalculationSettings? = null,
    var calculationParametersTables: CalculationParametersTables? = null
)

data class StartCalculationSettings(
    var balanceProductConfigs: List<BalanceProductConfig>? = null,
    var dealAttributes: List<String>? = null,
    var reportAttributes: List<String>? = null,
    var timeBucketSystemCustom: TimeBucketSystemCustom? = null,
    var reportDate: String? = null,
    var balanceFlag: String? = null,
)

data class BalanceProductConfig(
    var productType: String? = null,
)

data class TimeBucketSystemCustom(
    var tbsType: String? = null,
    var tbsCodes: List<String>? = null,
)
