package ru.romanow.camunda.service

import ru.romanow.camunda.domain.Calculation
import ru.romanow.camunda.domain.CalculationStatus
import ru.romanow.camunda.domain.enums.Status
import ru.romanow.camunda.models.CalculationStatusResponse
import java.util.*

interface CalculationStatusService {
    fun create(calculationUid: UUID, status: Status, opts: Map<String, String?> = mapOf()): CalculationStatus
    fun getLastStatus(calculationUid: UUID): String
}