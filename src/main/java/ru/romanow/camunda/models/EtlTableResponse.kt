package ru.romanow.camunda.models

data class EtlTableResponse(
    val database: String? = null,
    val schema: String? = null,
    val name: String? = null,
)