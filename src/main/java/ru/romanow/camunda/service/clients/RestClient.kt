package ru.romanow.camunda.service.clients

import ru.romanow.camunda.models.EtlResponse
import ru.romanow.camunda.models.PeriodResponse
import java.util.*

interface RestClient {
    fun migrate(calculationUid: UUID, itemUid: UUID, periods: List<PeriodResponse>): EtlResponse
}
