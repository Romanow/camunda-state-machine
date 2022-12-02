package ru.romanow.camunda.web

import org.camunda.bpm.engine.ProcessEngine
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/")
class ProcessController(
    private val processEngine: ProcessEngine
) {

    @PostMapping("/{processName}/start")
    fun start(@PathVariable processName: String, @RequestParam processId: Long): String {
        return processEngine.runtimeService
            .createProcessInstanceByKey(processName)
            .setVariable("processId", processId)
            .execute()
            .processInstanceId
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{processInstanceId}/approve/retry")
    fun approveRetry(@PathVariable processInstanceId: String) {
        approve(processInstanceId, "RetryApproveAction", mapOf("approveRetry" to true))
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/{processInstanceId}/approve/supervisor")
    fun adminApproval(@PathVariable processInstanceId: String) {
        approve(processInstanceId, "SupervisorApprovalAction", mapOf("supervisorApprove" to true))
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/{processInstanceId}/approve/manager")
    fun managerApproval(@PathVariable processInstanceId: String) {
        approve(processInstanceId, "ManagerApprovalAction", mapOf("managerApprove" to true))
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/{processInstanceId}/accept/{type}")
    fun approvalEvent(@PathVariable processInstanceId: String, @PathVariable type: String) {
        processEngine.runtimeService
            .createMessageCorrelation("$type approve")
            .processInstanceId(processInstanceId)
            .correlate()
    }

    fun approve(processInstanceId: String, taskType: String, params: Map<String, Any>) {
        val tasks = processEngine
            .taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskDefinitionKey(taskType)
            .list()

        tasks.forEach { processEngine.taskService.complete(it.id, params) }
    }
}