package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.config.properties.ReverseEtlProperties
import ru.romanow.camunda.models.LoadCalculationResultCommand
import ru.romanow.camunda.service.clients.DrpCommandClient
import ru.romanow.camunda.utils.AGGREGATION_REPORT_TABLE_NAME
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.OPERATION_UID
import ru.romanow.camunda.utils.get
import java.util.*

@Service("StartReverseEtlAction")
class StartReverseEtlAction(
    private val drpCommandClient: DrpCommandClient,
    private val reverseEtlProperties: ReverseEtlProperties,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val aggregationReportTableName: String = get(execution, AGGREGATION_REPORT_TABLE_NAME)

        logger.info("Request to DRP Command to start Reverse ETL process for '{}'", calculationUid)

        val request = LoadCalculationResultCommand(
            solveId = calculationUid,
            aggregationReportTableName = aggregationReportTableName,
            targetReportTableName = "${reverseEtlProperties.database}.${reverseEtlProperties.schema}.${reverseEtlProperties.table}"
        )
        val operationUid = drpCommandClient.loadResult(request)

        execution.setVariable(OPERATION_UID, operationUid)
    }
}