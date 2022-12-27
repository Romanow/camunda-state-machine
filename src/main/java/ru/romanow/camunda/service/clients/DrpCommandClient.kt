package ru.romanow.camunda.service.clients

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.camunda.config.properties.ServicesUrlProperties
import ru.romanow.camunda.exceptions.RestClientException
import ru.romanow.camunda.exceptions.buildEx
import ru.romanow.camunda.models.LoadCalculationResultCommand
import ru.romanow.camunda.models.StartCalculationCommand
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

    fun loadResult(request: LoadCalculationResultCommand) =
        webClient.post()
            .uri("${servicesUrlProperties.drpCommandUrl}/api/v1/drp/cash-flow/load-calc-result")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it.isError }) { response -> buildEx(response) { RestClientException(it) } }
            .bodyToMono(UUID::class.java)
            .block()!!

    fun startCalculation(request: StartCalculationCommand) =
        webClient.post()
            .uri("${servicesUrlProperties.drpCommandUrl}/api/v1/drp/cash-flow/start-calc")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it.isError }) { response -> buildEx(response) { RestClientException(it) } }
            .bodyToMono(UUID::class.java)
            .block()!!
}