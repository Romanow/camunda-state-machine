package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("ErrorAction")
class ErrorAction : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CopyDataToStagedAction::class.java)

    override fun execute(execution: DelegateExecution) {
        logger.error("ERROR!")
    }
}