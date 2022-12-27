package ru.romanow.camunda.actions

import org.apache.commons.lang3.StringUtils
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.config.properties.CommandProperties
import ru.romanow.camunda.models.*
import ru.romanow.camunda.service.CalculationService
import ru.romanow.camunda.service.clients.DrpCommandClient
import ru.romanow.camunda.utils.*
import java.time.format.DateTimeFormatter
import java.util.*

@Service("StartCalculationAction")
class StartCalculationAction(
    private val calculationService: CalculationService,
    private val drpCommandClient: DrpCommandClient,
    private val commandProperties: CommandProperties,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val incomingTables: Map<String, String> =
            fromJson(get(execution, CALCULATION_PARAMETERS_TABLES))

        val calculation = calculationService.getByUid(calculationUid)

        val balanceProductConfigs = commandProperties
            .settings!!
            .balanceProductConfig!!
            .filter { StringUtils.isNotEmpty(it) }
            .map { BalanceProductConfig(it) }

        val timeBucketSystemCustom = TimeBucketSystemCustom(
            tbsCodes = buildTableCodesFromPeriods(calculation.periods!!),
            tbsType = commandProperties.settings?.timeBucketSystemType
        )

        val settings = StartCalculationSettings(
            balanceProductConfigs = balanceProductConfigs,
            dealAttributes = commandProperties.settings?.dealAttributes,
            reportAttributes = commandProperties.settings?.reportAttributes,
            reportDate = calculation.startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            balanceFlag = commandProperties.settings?.balanceFlag,
            timeBucketSystemCustom = timeBucketSystemCustom
        )

        logger.info("Request to DRP Command to start calculation '{}'", calculationUid)

        val request = StartCalculationCommand(
            solveId = calculationUid,
            settings = settings,
            calculationParametersTables = buildCalculationParametersTables(incomingTables)
        )

        val operationUid = drpCommandClient.startCalculation(request)

        execution.setVariable(OPERATION_UID, operationUid)
    }

    private fun buildCalculationParametersTables(incomingTables: Map<String, String>): CalculationParametersTables {
        val tablesPath = mutableMapOf<String, String>()
            .also { it.putAll(commandProperties.tables!!) }

        val tables = mutableMapOf<String, String>()
        incomingTables.forEach { (p, h) -> tables[tablesPath[p]!!] = h }

        val macro = mutableMapOf<String, String>()
        val transferRate = mutableMapOf<String, String>()
        val products = mutableMapOf<String, String>()
        val resultTables = CalculationParametersTables()

        tables.forEach { (key: String, tablePath: String) ->
            when (key.lowercase()) {
                FXRATE -> macro[FXRATE] = tablePath
                MARKET_INDEX -> macro[MARKET_INDEX] = tablePath
                FTP -> transferRate[FTP] = tablePath
                VOLUME_FORECAST -> products[VOLUME_FORECAST] = tablePath
                ISSUES_WEIGHT -> products[ISSUES_WEIGHT] = tablePath
                ISSUES_WEIGHT_DISTRIBUTION -> products[ISSUES_WEIGHT_DISTRIBUTION] = tablePath
                INTEREST_MARGIN_RATES -> products[INTEREST_MARGIN_RATES] = tablePath
            }
        }
        resultTables.macro = macro
        resultTables.transferRates = transferRate
        resultTables.products = products

        return resultTables
    }

    private fun buildTableCodesFromPeriods(periods: List<PeriodResponse>): List<String> {
        val result: MutableList<String> = ArrayList()
        val lastIndex = periods.size - 1
        val firstMark = periods[0].mark
        val lastMark = periods[lastIndex].mark
        result.add("0D-$firstMark")
        for (i in 1 until lastIndex) {
            val mark = periods[i].mark
            val markBefore = periods[i - 1].mark
            result.add("$markBefore-$mark")
        }
        result.add("$lastMark-INF")
        return result
    }

    companion object {
        private const val FXRATE = "fxrate"
        private const val MARKET_INDEX = "market-index"
        private const val FTP = "ftp"
        private const val VOLUME_FORECAST = "volume-forecast"
        private const val ISSUES_WEIGHT = "issues-weight"
        private const val ISSUES_WEIGHT_DISTRIBUTION = "issues-weight-distribution"
        private const val INTEREST_MARGIN_RATES = "interest-margin-rates"
    }
}