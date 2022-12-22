package ru.romanow.camunda.models

import javax.validation.constraints.NotEmpty

data class CreateCalculationRequest(
    @field:NotEmpty
    val name: String? = null,
    val description: String? = null,
)
