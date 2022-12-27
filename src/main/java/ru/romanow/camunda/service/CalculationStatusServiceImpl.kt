package ru.romanow.camunda.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.camunda.domain.CalculationStatus
import ru.romanow.camunda.domain.enums.Status
import ru.romanow.camunda.repository.CalculationRepository
import ru.romanow.camunda.repository.CalculationStatusRepository
import ru.romanow.camunda.utils.OPERATION_UID
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class CalculationStatusServiceImpl(
    private val calculationRepository: CalculationRepository,
    private val calculationStatusRepository: CalculationStatusRepository,
) : CalculationStatusService {

    @Transactional
    override fun create(calculationUid: UUID, status: Status, opts: Map<String, String?>): CalculationStatus {
        val calculation = calculationRepository.findByUid(calculationUid)
            .orElseThrow { EntityNotFoundException("Calculation '$calculationUid' not found") }

        return CalculationStatus(
            status = status,
            operationUid = opts[OPERATION_UID],
            calculation = calculation,
        ).also { calculationStatusRepository.save(it) }
    }

    @Transactional(readOnly = true)
    override fun getLastStatus(calculationUid: UUID): String =
        calculationStatusRepository
            .getStatuses(calculationUid, Pageable.ofSize(1))
            .first()
            .status!!.name
}