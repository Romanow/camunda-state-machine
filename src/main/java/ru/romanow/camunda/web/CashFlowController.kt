package ru.romanow.camunda.web

import org.springframework.web.bind.annotation.*
import ru.romanow.camunda.models.AirflowResponse
import ru.romanow.camunda.models.CalculationResponse
import ru.romanow.camunda.models.CreateCalculationRequest
import ru.romanow.camunda.service.CalculationManagementService
import ru.romanow.camunda.service.CalculationService
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/v1/cashflow/calculation")
class CashFlowController(
    private val calculationService: CalculationService,
    private val calculationManagementService: CalculationManagementService,
) {

    @GetMapping("/{calculationUid}")
    fun getByUid(@PathVariable calculationUid: UUID): CalculationResponse =
        calculationService.getByUid(calculationUid)

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCalculationRequest) =
        calculationManagementService.createAndStartCalculation(request)

    @PostMapping("/{calculationUid}/answer-from-drp")
    fun answerFromDrp(
        @PathVariable calculationUid: UUID,
        @Valid @RequestBody response: AirflowResponse,
    ): CalculationResponse =
        calculationManagementService.processFromDrp(calculationUid, response)
}