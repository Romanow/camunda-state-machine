package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.service.clients.DrpCommandClient
import ru.romanow.camunda.utils.CALCULATION_PARAMETERS_TABLES
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.fromJson
import ru.romanow.camunda.utils.get
import java.util.*

@Service("StartCalculationAction")
class StartCalculationAction(
    private val drpCommandClient: DrpCommandClient,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val calculationParametersTables: Map<String, String> =
            fromJson(get(execution, CALCULATION_PARAMETERS_TABLES))

    }
}