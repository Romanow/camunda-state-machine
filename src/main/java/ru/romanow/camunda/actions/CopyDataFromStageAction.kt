package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.utils.CALCULATION_UID
import ru.romanow.camunda.utils.get
import java.util.*

@Service("CopyDataFromStageAction")
class CopyDataFromStageAction : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataFromStageAction::class.java)

    override fun execute(execution: DelegateExecution) {
        val calculationUid: UUID = get(execution, CALCULATION_UID)
    }
}