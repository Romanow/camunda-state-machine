package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Service
import ru.romanow.camunda.domain.enums.Status.CALCULATION_ERROR
import ru.romanow.camunda.service.CalculationStatusService
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.OPERATION_UID
import ru.romanow.camunda.utils.get
import java.util.*

@Service("ErrorAction")
class ErrorAction(
    private val calculationStatusService: CalculationStatusService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
        val operationUid: UUID? = get(execution, OPERATION_UID)

        calculationStatusService.create(
            calculationUid,
            CALCULATION_ERROR,
            mapOf(OPERATION_UID to operationUid.toString())
        )
    }
}