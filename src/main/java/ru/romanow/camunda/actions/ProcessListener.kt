package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.delegate.Expression
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.romanow.camunda.domain.enums.Status
import ru.romanow.camunda.service.CalculationStatusService
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.OPERATION_UID
import ru.romanow.camunda.utils.get
import java.util.*

@Component("ProcessListener")
class ProcessListener(
    private val calculationStatusService: CalculationStatusService,
) : ExecutionListener {
    private val logger = LoggerFactory.getLogger(ProcessListener::class.java)

    private var status: Expression? = null
    private var operationUid: Expression? = null

    override fun notify(execution: DelegateExecution) {
        val statusValue = status?.getValue(execution).toString()
        val operationUidValue = operationUid?.getValue(execution).toString()

        val calculationUid: UUID = get(execution, CALCULATION_UID)

        logger.info("Process '{}' set status '{}'", statusValue, calculationUid)

        calculationStatusService.create(
            calculationUid = calculationUid,
            status = Status.valueOf(statusValue),
            mapOf(OPERATION_UID to operationUidValue)
        )
    }
}