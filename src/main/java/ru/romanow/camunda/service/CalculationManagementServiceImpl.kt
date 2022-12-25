package ru.romanow.camunda.service

import org.camunda.bpm.engine.ProcessEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.utils.*
import java.util.*

@Service
class CalculationManagementServiceImpl(
    private val calculationService: CalculationService,
    private val processEngine: ProcessEngine,
) : CalculationManagementService {
    private val logger = LoggerFactory.getLogger(CalculationManagementServiceImpl::class.java)

    override fun createAndStartCalculation(request: CreateCalculationRequest): UUID {
        val calculation = calculationService.create(request)

        processEngine.runtimeService
            .createProcessInstanceByKey(PROCESS_NAME)
            .setVariables(mapOf(
                CALCULATION_UID to calculation.uid,
                MACRO_UID to calculation.macroUid,
                TRANSFER_RATE_UID to calculation.transferRateUid,
                PRODUCT_SCENARIO_UID to calculation.productScenarioUid
            ))
            .execute()

        return calculation.uid!!
    }
}