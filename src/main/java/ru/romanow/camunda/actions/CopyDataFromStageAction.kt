package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.service.clients.BalanceResultClient
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.OPERATION_UID
import ru.romanow.camunda.utils.get
import java.util.*

@Service("CopyDataFromStageAction")
class CopyDataFromStageAction(
    private val balanceResultClient: BalanceResultClient,
) : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataFromStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        logger.info("Request to holder to move from stage schema '{}'", calculationUid)
        val operationUid = balanceResultClient.migrate(calculationUid);
        execution.setVariable(OPERATION_UID, operationUid)
    }
}