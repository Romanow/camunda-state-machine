package ru.romanow.camunda.service

import org.springframework.stereotype.Service
import ru.romanow.camunda.repository.CalculationStatusRepository

@Service
class CalculationStatusServiceImpl(
    private val calculationStatusRepository: CalculationStatusRepository,
) : CalculationStatusService {

}