package ru.romanow.camunda.actions

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.camunda.utils.PROCESS_ID
import ru.romanow.camunda.utils.PROCESS_STATUS
import ru.romanow.camunda.utils.Status

@Service("CheckRequestAction")
class CheckRequestAction : JavaDelegate {
    private val logger = LoggerFactory.getLogger(CheckRequestAction::class.java)
    private val processes = mutableMapOf<Long, Int>()

    override fun execute(execution: DelegateExecution) {
        val processId = execution.getVariable(PROCESS_ID) as Long
        var counter = processes.computeIfAbsent(processId) { 0 }
        processes[processId] = ++counter

        logger.info("Process {} wait iteration {}", processId, counter)

        val status = if (counter >= 10) Status.DONE else if (counter == 5) Status.ERROR else Status.IN_PROGRESS
        execution.setVariable(PROCESS_STATUS, status.name)
    }
}