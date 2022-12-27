package ru.romanow.camunda.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.camunda.domain.Calculation
import ru.romanow.camunda.domain.enums.CalculationType
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.repository.CalculationRepository
import ru.romanow.camunda.utils.UUIDGenerator
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class CalculationServiceImpl(
    private val calculationRepository: CalculationRepository,
    private val calculationPeriodService: CalculationPeriodService,
    private val calculationStatusService: CalculationStatusService,
    private val generator: UUIDGenerator,
) : CalculationService {
    private val logger = LoggerFactory.getLogger(CalculationServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getByUid(calculationUid: UUID): CalculationResponse =
        calculationRepository.findByUid(calculationUid)
            .map { buildCalculationResponse(it) }
            .orElseThrow { EntityNotFoundException("Calculation '$calculationUid' not found") }

    @Transactional
    override fun create(request: CreateCalculationRequest): Calculation {
        var calculation = Calculation(
            uid = generator.generate(),
            name = request.name,
            description = request.description,
            type = CalculationType.CASHFLOW,
            startDate = request.startDate,
            macroUid = request.macroUid,
            transferRateUid = request.transferRateUid,
            productScenarioUid = request.productScenarioUid
        )

        calculation = calculationRepository.save(calculation)
        calculationPeriodService.create(request.periods!!, calculation)

        return calculation
    }

    private fun buildCalculationResponse(calculation: Calculation) =
        CalculationResponse(
            uid = calculation.uid,
            name = calculation.name,
            description = calculation.description,
            type = calculation.type,
            startDate = calculation.startDate,
            status = calculationStatusService.getLastStatus(calculation.uid!!),
            periods = calculationPeriodService.findByCalculationUid(calculation.uid!!),
            macroUid = calculation.macroUid,
            transferRateUid = calculation.transferRateUid,
            productScenarioUid = calculation.productScenarioUid,
            createdDate = calculation.createdDate,
            modifiedDate = calculation.modifiedDate
        )
}