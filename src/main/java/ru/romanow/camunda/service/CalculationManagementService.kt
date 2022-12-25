package ru.romanow.camunda.service

import ru.romanow.camunda.models.CreateCalculationRequest
import java.util.*

interface CalculationManagementService {
    fun createAndStartCalculation(request: CreateCalculationRequest): UUID
}