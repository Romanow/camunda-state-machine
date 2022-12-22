package ru.romanow.camunda.models

data class ValidationErrorResponse(
    var message: String,
    var errors: Map<String, String>,
)
