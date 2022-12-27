package ru.romanow.camunda.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "command")
data class CommandProperties(
    var settings: SettingsProperties? = null,
    var tables: Map<String, String>? = null,
)
