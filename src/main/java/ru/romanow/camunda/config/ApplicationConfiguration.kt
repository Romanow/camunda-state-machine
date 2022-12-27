package ru.romanow.camunda.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.romanow.camunda.config.properties.ReverseEtlProperties

@Configuration
@EnableConfigurationProperties(ReverseEtlProperties::class)
class ApplicationConfiguration