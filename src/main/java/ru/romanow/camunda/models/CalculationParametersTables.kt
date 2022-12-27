package ru.romanow.camunda.models

data class CalculationParametersTables(
    var macro: Map<String, String>? = null,
    var ts: Map<String, String>? = null,
    var products: Map<String, String>? = null,
)
