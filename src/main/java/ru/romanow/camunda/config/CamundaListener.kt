package ru.romanow.camunda.config;

import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class CamundaListener {
    private static final Logger logger = getLogger(CamundaListener.class);

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        logger.info("Handling immutable ExecutionEvent:{}", executionEvent.getProcessDefinitionId());
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        logger.info("Handling immutable TaskEvent:{}", taskEvent.getTaskDefinitionKey());
    }
}
