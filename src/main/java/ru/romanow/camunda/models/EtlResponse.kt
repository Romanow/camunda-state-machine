package ru.romanow.camunda.models

data class EtlResponse(
    var etlTableList: List<EtlTableResponse>? = null,
)