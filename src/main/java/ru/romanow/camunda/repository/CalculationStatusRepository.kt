package ru.romanow.camunda.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.camunda.domain.CalculationStatus

interface CalculationStatusRepository : JpaRepository<CalculationStatus, Long>