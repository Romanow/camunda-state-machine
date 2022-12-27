package ru.romanow.camunda.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.romanow.camunda.config.properties.CommandProperties
import ru.romanow.camunda.config.properties.ReverseEtlProperties
import ru.romanow.camunda.config.properties.SettingsProperties

@Configuration
@EnableConfigurationProperties(value = [
    ReverseEtlProperties::class,
    SettingsProperties::class,
    CommandProperties::class
])
class ApplicationConfiguration