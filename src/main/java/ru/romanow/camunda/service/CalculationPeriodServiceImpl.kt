package ru.romanow.camunda.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.camunda.domain.Calculation
import ru.romanow.camunda.domain.CalculationPeriod
import ru.romanow.camunda.models.PeriodRequest
import ru.romanow.camunda.models.PeriodResponse
import ru.romanow.camunda.repository.CalculationPeriodRepository
import java.util.*

@Service
class CalculationPeriodServiceImpl(
    private val calculationPeriodRepository: CalculationPeriodRepository,
) : CalculationPeriodService {

    @Transactional
    override fun create(periods: List<PeriodRequest>, calculation: Calculation) {
        calculationPeriodRepository.saveAll(
            periods.map {
                CalculationPeriod(startDate = it.startDate,
                    endDate = it.endDate,
                    mark = it.mark,
                    serialNumber = it.serialNumber,
                    calculation = calculation)
            }
        )
    }

    @Transactional(readOnly = true)
    override fun findByCalculationUid(calculationUid: UUID) =
        calculationPeriodRepository
            .findByCalculationUid(calculationUid)
            .map {
                PeriodResponse(startDate = it.startDate,
                    endDate = it.endDate,
                    mark = it.mark,
                    serialNumber = it.serialNumber)
            }
}
