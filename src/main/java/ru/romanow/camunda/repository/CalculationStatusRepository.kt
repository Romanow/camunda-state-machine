package ru.romanow.camunda.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.romanow.camunda.domain.CalculationStatus
import java.util.*

interface CalculationStatusRepository : JpaRepository<CalculationStatus, Long> {
    @Query("""
        select cs 
        from CalculationStatus cs 
        where cs.calculation.uid = :calculationUid 
        order by cs.createdDate desc 
    """)
    fun getLastStatus(@Param("calculationUid") calculationUid: UUID, pageable: Pageable): List<CalculationStatus>
}