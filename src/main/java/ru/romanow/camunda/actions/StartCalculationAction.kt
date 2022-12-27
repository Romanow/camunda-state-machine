package ru.romanow.camunda.actions

import org.apache.commons.lang3.StringUtils
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.domain.CalculationPeriod
import ru.romanow.camunda.models.CalculationParametersTables
import ru.romanow.camunda.models.StartCalculationCommand
import ru.romanow.camunda.service.CalculationService
import ru.romanow.camunda.service.clients.DrpCommandClient
import ru.romanow.camunda.utils.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Service("StartCalculationAction")
class StartCalculationAction(
    private val calculationService: CalculationService,
    private val drpCommandClient: DrpCommandClient,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val calculationParametersTables: Map<String, String> =
            fromJson(get(execution, CALCULATION_PARAMETERS_TABLES))

        val calculation = calculationService.getByUid(calculationUid)
        val factDate = calculation.createdDate!!.toLocalDate()

        val balanceProductConfs: List<StartCalculationCommand.Settings.BalanceProductConf> = commandSettingProperties
            .getBalanceProductConf()
            .stream()
            .filter { cs: CharSequence? -> StringUtils.isNotEmpty(cs) }
            .map { StartCalculationCommand.Settings.BalanceProductConf() }
            .collect(Collectors.toList())

        val timeBucketSystemCustom: StartCalculationCommand.TimeBucketSystemCustom = TimeBucketSystemCustom()
            .setTbsCodes(getTbsCodesFromPeriods(calculationEntity.getCalculationPeriods()))
            .setTbsType(commandSettingProperties.getTimeBucketSystemType())

        val settings: StartCalculationCommand.Settings = Settings()
            .setBalanceProductConf(balanceProductConfs)
            .setDealAttributes(commandSettingProperties.getDealAttributes())
            .setReportAttributes(commandSettingProperties.getReportAttributes())
            .setReportDate(factDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
            .setBalanceFlag(commandSettingProperties.getBalanceFlag())
            .setTimeBucketSystemCustom(timeBucketSystemCustom)

        val calculationParametersTables = buildCalculationParametersTables(calculationParametersTables)
        val request = StartCalculationCommand(
            solveId = calculationUid,
            settings = settings,
            calculationParametersTables = calculationParametersTables
        )

        val operationUid = drpCommandClient.startCalculation(request)

        execution.setVariable(OPERATION_UID, operationUid)
    }

    private fun buildCalculationParametersTables(incomingTables: Map<String, String>): CalculationParametersTables {
        val tablesPath = mutableMapOf<String, String>()

        commandTablesProperties.getTables()
            .forEach { key, path ->
                tablesPath.put(path, key)
            }

        val tables = mutableMapOf<String, String>()
        incomingTables
            .forEach { (postgresTablePath: String, hadoopTablePath: String) ->
                val key = tablesPath[postgresTablePath]
                tables[key] = hadoopTablePath
            }
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

    private fun getTbsCodesFromPeriods(periods: List<CalculationPeriod>): List<String> {
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
        private const val MARKET_INDEX = "market_index"
        private const val FTP = "ftp"
        private const val VOLUME_FORECAST = "volume_forecast"
        private const val ISSUES_WEIGHT = "issues_weight"
        private const val ISSUES_WEIGHT_DISTRIBUTION = "issues_weight_distribution"
        private const val INTEREST_MARGIN_RATES = "interest_margin_rates"
    }
}