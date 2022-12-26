package ru.romanow.camunda.service

import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import java.util.*

interface CalculationManagementService {
    fun createAndStartCalculation(request: CreateCalculationRequest): UUID
    fun processFromDrp(calculationUid: UUID, response: AirflowResponse): CalculationResponse
}