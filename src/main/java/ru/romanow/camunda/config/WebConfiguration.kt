package ru.romanow.camunda.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.camunda.config.properties.ServicesUrlProperties

@Configuration
@EnableConfigurationProperties(ServicesUrlProperties::class)
class WebConfiguration {

    @Bean
    fun webClient(): WebClient = WebClient.create()
}