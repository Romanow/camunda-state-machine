package ru.romanow.camunda.exceptions

import org.camunda.bpm.engine.delegate.BpmnError
import ru.romanow.camunda.utils.REST_CLIENT_ERROR

class RestClientException(message: String?) : BpmnError(REST_CLIENT_ERROR, message)