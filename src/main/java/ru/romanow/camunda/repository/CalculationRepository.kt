package ru.romanow.camunda.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.camunda.domain.Calculation
import java.util.*

interface CalculationRepository : JpaRepository<Calculation, Long> {
    fun findByUid(uid: UUID): Optional<Calculation>
}