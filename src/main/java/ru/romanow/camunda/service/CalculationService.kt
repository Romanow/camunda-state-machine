package ru.romanow.camunda.service

import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import java.util.*

interface CalculationService {
    fun getByUid(calculationUid: UUID): CalculationResponse
    fun create(request: CreateCalculationRequest): UUID
    fun processFromDrp(request: AirflowResponse): CalculationResponse
}