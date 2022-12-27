package ru.romanow.camunda.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "services")
data class ServicesUrlProperties(
    val macroScenarioUrl: String? = null,
    val transferRateUrl: String? = null,
    val productScenarioUrl: String? = null,
    val drpCommandUrl: String? = null,
    val balanceResultUrl: String? = null,
)