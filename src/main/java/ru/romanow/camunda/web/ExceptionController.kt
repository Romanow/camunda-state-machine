package ru.romanow.camunda.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.romanow.camunda.models.ErrorResponse
import ru.romanow.camunda.models.ValidationErrorResponse
import javax.persistence.EntityNotFoundException

@RestControllerAdvice
class ExceptionController {
    private val logger = LoggerFactory.getLogger(ExceptionController::class.java)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun badRequest(exception: MethodArgumentNotValidException): ValidationErrorResponse {
        val validationErrors = prepareValidationErrors(exception.bindingResult.fieldErrors)
        logger.info("Bad Request '{}'", validationErrors)
        return ValidationErrorResponse("Validation failed", validationErrors)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(exception: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException::class)
    fun notFound(exception: RuntimeException): ErrorResponse {
        logger.error("", exception)
        return ErrorResponse(exception.message)
    }

    private fun prepareValidationErrors(errors: List<FieldError>) =
        errors.associateBy(
            { err -> err.field },
            { err -> "Field has wrong value ${err.rejectedValue}: ${err.defaultMessage}" }
        ).toMap()

}