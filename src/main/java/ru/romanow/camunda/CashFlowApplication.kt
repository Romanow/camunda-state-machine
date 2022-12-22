package ru.romanow.camunda

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class CashFlowApplication

fun main(args: Array<String>) {
    SpringApplication.run(CashFlowApplication::class.java, *args)
}