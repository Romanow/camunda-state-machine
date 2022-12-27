package ru.romanow.camunda.service.clients

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.camunda.config.properties.ServicesUrlProperties
import ru.romanow.camunda.exceptions.RestClientException
import ru.romanow.camunda.exceptions.buildEx
import java.util.*

@Service
class BalanceResultClient(
    private val servicesUrlProperties: ServicesUrlProperties,
    private val webClient: WebClient,
) {

    fun migrate(calculationUid: UUID) =
        webClient.post()
            .uri("${servicesUrlProperties.macroScenarioUrl}/api/v1/balance/migration")
            .retrieve()
            .onStatus({ it.isError }) { response -> buildEx(response) { RestClientException(it) } }
            .bodyToMono(UUID::class.java)
            .block()!!
}