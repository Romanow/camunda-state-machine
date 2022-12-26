package ru.romanow.camunda.service.clients

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.camunda.config.properties.ServicesUrlProperties
import ru.romanow.camunda.exceptions.RestClientException
import ru.romanow.camunda.exceptions.buildEx
import ru.romanow.camunda.models.UploadCashflowParametersCommand
import java.util.*

@Service
class DrpCommandClient(
    private val servicesUrlProperties: ServicesUrlProperties,
    private val webClient: WebClient,
) {

    fun copyParams(request: UploadCashflowParametersCommand) =
        webClient.post()
            .uri("${servicesUrlProperties.drpCommandUrl}/api/v1/drp/cash-flow/copy-params")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it.isError }) { response -> buildEx(response) { RestClientException(it) } }
            .bodyToMono(UUID::class.java)
            .block()!!
}