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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{processInstanceId}/approve/admin")
    fun adminApproval(@PathVariable processInstanceId: String) {
        approve(processInstanceId, "AdminApprovalAction", mapOf("adminApprove" to true))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{processInstanceId}/approve/manager")
    fun managerApproval(@PathVariable processInstanceId: String) {
        approve(processInstanceId, "ManagerApprovalAction", mapOf("managerApprove" to true))
    }

    fun approve(processInstanceId: String, taskType: String, params: Map<String, Any>) {
        val task = processEngine
            .taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskDefinitionKey(taskType)
            .singleResult()

        processEngine.taskService.complete(task.id, params)
    }
}