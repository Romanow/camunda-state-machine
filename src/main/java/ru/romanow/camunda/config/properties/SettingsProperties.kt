package ru.romanow.camunda.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "command.settings")
data class SettingsProperties(
    var balanceProductConfig: List<String>? = null,
    var dealAttributes: List<String>? = null,
    var reportAttributes: List<String>? = null,
    var timeBucketSystemType: String? = null,
    var balanceFlag: String? = null,
)
