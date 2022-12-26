package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.exceptions.RestClientException
import ru.romanow.camunda.service.CalculationPeriodService
import ru.romanow.camunda.service.clients.MacroScenarioClient
import ru.romanow.camunda.service.clients.ProductScenarioClient
import ru.romanow.camunda.service.clients.TransferRateClient
import ru.romanow.camunda.utils.*
import java.util.*

@Service("CopyDataToStagedAction")
class CopyDataToStagedAction(
    private val calculationPeriodService: CalculationPeriodService,
    private val productScenarioClient: ProductScenarioClient,
    private val macroScenarioClient: MacroScenarioClient,
    private val transferRateClient: TransferRateClient,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStagedAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val macroUid: UUID = get(execution, MACRO_UID)
        val transferRateUid: UUID = get(execution, TRANSFER_RATE_UID)
        val productScenarioUid: UUID = get(execution, PRODUCT_SCENARIO_UID)

        val periods = calculationPeriodService.findByCalculationUid(calculationUid)

        logger.info("Process periods $periods for calculation '$calculationUid'")

        macroScenarioClient
            .migrate(calculationUid, macroUid, periods)
            .let { execution.setVariable(MACRO_TABLES, toJson(it.etlTableList)) }

        transferRateClient
            .migrate(calculationUid, transferRateUid, periods)
            .let { execution.setVariable(TRANSFER_RATE_TABLES, toJson(it.etlTableList)) }

        productScenarioClient
            .migrate(calculationUid, productScenarioUid, periods)
            .let { execution.setVariable(PRODUCT_SCENARIO_TABLES, toJson(it.etlTableList)) }

    }

    private fun <T> get(execution: DelegateExecution, name: String): T =
        execution.getVariable(name) as T
}