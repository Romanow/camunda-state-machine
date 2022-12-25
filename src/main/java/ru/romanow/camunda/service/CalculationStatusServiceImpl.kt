package ru.romanow.camunda.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.camunda.repository.CalculationStatusRepository
import java.util.*

@Service
class CalculationStatusServiceImpl(
    private val calculationStatusRepository: CalculationStatusRepository,
) : CalculationStatusService {

    @Transactional(readOnly = true)
    override fun getLastStatus(calculationUid: UUID) =
        calculationStatusRepository
            .getLastStatus(calculationUid, Pageable.ofSize(1))
            .first()
}