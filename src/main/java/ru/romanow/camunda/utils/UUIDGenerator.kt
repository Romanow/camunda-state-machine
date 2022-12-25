package ru.romanow.camunda.utils

import org.springframework.stereotype.Component
import java.util.*

@Component
class UUIDGenerator {
    fun generate() = UUID.randomUUID()
}