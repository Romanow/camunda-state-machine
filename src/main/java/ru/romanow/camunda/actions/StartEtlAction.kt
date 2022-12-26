package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.models.EtlTableResponse
import ru.romanow.camunda.models.UploadCashflowParametersCommand
import ru.romanow.camunda.service.clients.DrpCommandClient
import ru.romanow.camunda.utils.*
import java.util.*

@Service("StartEtlAction")
class StartEtlAction(
    private val drpCommandClient: DrpCommandClient,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val macroTables: List<EtlTableResponse> = fromJson(get<String>(execution, MACRO_TABLES))
        val transferRateTables: List<EtlTableResponse> = fromJson(get(execution, TRANSFER_RATE_TABLES))
        val productScenarioTables: List<EtlTableResponse> = fromJson(get(execution, PRODUCT_SCENARIO_TABLES))

        val tables: List<String> =
            listOf(macroTables, transferRateTables, productScenarioTables)
                .flatten()
                .map { "${it.database}.${it.schema}.${it.name}" }

        logger.info("Request to DRP Command to init ETL process for tables '{}'", tables)

        val request = UploadCashflowParametersCommand(
            solveId = calculationUid,
            calculationSourceTables = tables
        )

        val operationUid = drpCommandClient.copyParams(request)

        execution.setVariable(OPERATION_UID, operationUid)
    }
}