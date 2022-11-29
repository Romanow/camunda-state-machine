package ru.romanow.camunda

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class CamundaStateMachineApplication

fun main(args: Array<String>) {
    SpringApplication.run(CamundaStateMachineApplication::class.java, *args)
}