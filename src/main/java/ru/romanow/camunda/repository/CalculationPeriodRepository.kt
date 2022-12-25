package ru.romanow.camunda.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.romanow.camunda.domain.CalculationPeriod
import java.util.*

interface CalculationPeriodRepository : JpaRepository<CalculationPeriod, Long> {

    @Query("select p from CalculationPeriod p where p.calculation.uid = :calculationUid")
    fun findByCalculationUid(@Param("calculationUid") calculationUid: UUID): List<CalculationPeriod>
}