package ru.romanow.camunda.service

import org.camunda.bpm.engine.ProcessEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.utils.*
import java.util.*

@Service
class CalculationManagementServiceImpl(
    private val calculationService: CalculationService,
    private val processEngine: ProcessEngine,
) : CalculationManagementService {
    private val logger = LoggerFactory.getLogger(CalculationManagementServiceImpl::class.java)

    override fun createAndStartCalculation(request: CreateCalculationRequest): CalculationResponse {
        val calculation = calculationService.create(request)

        processEngine.runtimeService
            .createProcessInstanceByKey(PROCESS_NAME)
            .businessKey(calculation.uid.toString())
            .setVariables(mapOf(
                CALCULATION_UID to calculation.uid,
                MACRO_UID to calculation.macroUid,
                TRANSFER_RATE_UID to calculation.transferRateUid,
                PRODUCT_SCENARIO_UID to calculation.productScenarioUid
            ))
            .execute()

        return calculationService.getByUid(calculation.uid!!)
    }

    override fun processFromDrp(calculationUid: UUID, response: AirflowResponse): CalculationResponse {
        val instance = processEngine
            .runtimeService
            .createProcessInstanceQuery()
            .processInstanceBusinessKey(calculationUid.toString())
            .active()
            .singleResult()
            .processInstanceId

        val event = when (response.status) {
            UPLOADED -> ETL_RESULT_EVENT
            SUCCESS -> CALCULATION_RESULT_EVENT
            UPLOAD_FINISHED -> REVERSE_ETL_RESULT_EVENT
            else -> ERROR_EVENT
        }

        processEngine
            .runtimeService
            .createMessageCorrelation(event)
            .processInstanceId(instance)
            .setVariables(mapOf(
                AGGREGATION_REPORT_TABLE_NAME to response.aggReportTableName,
                CALCULATION_PARAMETERS_TABLES to toJson(response.calculationParametersTables)
            ))
            .correlate()

        return calculationService.getByUid(calculationUid)
    }
}