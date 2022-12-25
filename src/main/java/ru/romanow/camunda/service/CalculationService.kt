package ru.romanow.camunda.service

import ru.romanow.camunda.domain.Calculation
import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import java.util.*

interface CalculationService {
    fun getByUid(calculationUid: UUID): CalculationResponse
    fun create(request: CreateCalculationRequest): Calculation
    fun processFromDrp(request: AirflowResponse): CalculationResponse
}