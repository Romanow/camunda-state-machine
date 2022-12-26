package ru.romanow.camunda.config

import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class CamundaListener {
    private val logger = LoggerFactory.getLogger(CamundaListener::class.java)

    @EventListener
    fun onExecutionEvent(event: ExecutionEvent) {
        logger.info("Handling event: {}: {}", event.currentActivityId, event.eventName)
    }
}