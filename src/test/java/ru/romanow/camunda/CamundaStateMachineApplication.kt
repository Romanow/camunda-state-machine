package ru.romanow.camunda

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import ru.romanow.camunda.config.DatabaseTestConfiguration

@SpringBootTest(classes = [DatabaseTestConfiguration::class])
internal class CamundaStateMachineApplication {

    @Test
    fun runApp() {
    }
}