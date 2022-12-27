package ru.romanow.camunda.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "reverse.etl")
data class ReverseEtlProperties(
    var database: String? = null,
    var schema: String? = null,
    var table: String? = null,
)
