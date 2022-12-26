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
            .map {
                CalculationResponse(
                    uid = it.uid,
                    name = it.name,
                    description = it.description,
                    type = it.type,
                    status = calculationStatusService.getLastStatus(it.uid!!),
                    periods = calculationPeriodService.findByCalculationUid(it.uid!!),
                    createdDate = it.createdDate,
                    modifiedDate = it.modifiedDate
                )
            }
            .orElseThrow { EntityNotFoundException("Calculation '$calculationUid' not found") }

    @Transactional
    override fun create(request: CreateCalculationRequest): Calculation {
        var calculation = Calculation(
            uid = generator.generate(),
            name = request.name,
            description = request.description,
            type = CalculationType.CASHFLOW,
            macroUid = generator.generate(),
            transferRateUid = generator.generate(),
            productScenarioUid = generator.generate()
        )

        calculation = calculationRepository.save(calculation)
        calculationPeriodService.create(request.periods!!, calculation)

        return calculation
    }
}