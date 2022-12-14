package ru.romanow.camunda.service.clients

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.camunda.config.properties.ServicesUrlProperties
import ru.romanow.camunda.exceptions.RestClientException
import ru.romanow.camunda.exceptions.buildEx
import ru.romanow.camunda.models.EtlResponse
import ru.romanow.camunda.models.PeriodResponse
import java.util.*

@Service
class ProductScenarioClient(
    private val servicesUrlProperties: ServicesUrlProperties,
    private val webClient: WebClient,
) : RestClient {

    override fun migrate(calculationUid: UUID, itemUid: UUID, periods: List<PeriodResponse>) =
        webClient.post()
            .uri("${servicesUrlProperties.productScenarioUrl}/api/v1/product-scenario/migration-to-staged") {
                it.queryParam("calculationVersionUid", calculationUid)
                    .queryParam("scenarioUid", itemUid)
                    .build()
            }
            .body(BodyInserters.fromValue(periods))
            .retrieve()
            .onStatus({ it.isError }) { response -> buildEx(response) { RestClientException(it) } }
            .bodyToMono(EtlResponse::class.java)
            .block()!!
}