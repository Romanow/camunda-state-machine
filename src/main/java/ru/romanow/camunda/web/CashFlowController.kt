package ru.romanow.camunda.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest
import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.service.CalculationService
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/v1/cashflow/calculation")
class CashFlowController(
    private val calculationService: CalculationService,
) {

    @GetMapping("/{calculationUid}")
    fun getByUid(@PathVariable calculationUid: UUID): CalculationResponse = calculationService.getByUid(calculationUid)

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCalculationRequest): ResponseEntity<Void> {
        val calculationUid = calculationService.create(request)
        return ResponseEntity.created(
            fromCurrentRequest()
                .path("/{uid}")
                .buildAndExpand(calculationUid)
                .toUri()
        ).build()
    }

    @PostMapping("/answer-from-drp")
    fun answerFromDrp(@Valid @RequestBody request: AirflowResponse): CalculationResponse =
        calculationService.processFromDrp(request)
}