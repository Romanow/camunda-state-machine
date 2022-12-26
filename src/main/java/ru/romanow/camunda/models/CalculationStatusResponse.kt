package ru.romanow.camunda.models

import ru.romanow.camunda.domain.enums.Status
import java.time.LocalDateTime

data class CalculationStatusResponse(
    val status: Status? = null,
    val operationUid: String? = null,
    val createdDate: LocalDateTime? = null,
)