package ru.romanow.camunda

import org.camunda.bpm.engine.spring.annotations.ProcessEngineComponent
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableProcessApplication
class CamundaStateMachineApplication

fun main(args: Array<String>) {
    SpringApplication.run(CamundaStateMachineApplication::class.java, *args)
}