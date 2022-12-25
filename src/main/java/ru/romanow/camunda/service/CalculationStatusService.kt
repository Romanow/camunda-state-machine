package ru.romanow.camunda.service

import ru.romanow.camunda.domain.CalculationStatus
import java.util.*

interface CalculationStatusService {
    fun getLastStatus(calculationUid: UUID): CalculationStatus
}