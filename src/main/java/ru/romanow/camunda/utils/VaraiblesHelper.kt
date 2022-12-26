package ru.romanow.camunda.utils

import org.camunda.bpm.engine.delegate.DelegateExecution

fun <T> get(execution: DelegateExecution, name: String): T =
    execution.getVariable(name) as T