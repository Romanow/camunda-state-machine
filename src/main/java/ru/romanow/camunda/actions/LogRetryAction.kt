package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("LogRetryAction")
class LogRetryAction : JavaDelegate {
    private val logger = LoggerFactory.getLogger(LogRetryAction::class.java)

    override fun execute(execution: DelegateExecution) {
        logger.info("{}", execution)
    }
}