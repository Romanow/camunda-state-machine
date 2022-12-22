package ru.romanow.camunda.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.repository.CalculationRepository
import java.util.*

@Service
class CalculationServiceImpl(
    private val calculationRepository: CalculationRepository,
) : CalculationService {
    private val logger = LoggerFactory.getLogger(CalculationServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getByUid(calculationUid: UUID): CalculationResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun create(request: CreateCalculationRequest): UUID {
        TODO("Not yet implemented")
    }

    override fun processFromDrp(request: AirflowResponse): CalculationResponse {
        TODO("Not yet implemented")
    }
}