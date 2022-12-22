package ru.romanow.camunda.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.camunda.domain.enums.Status
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "calculation_status")
@EntityListeners(AuditingEntityListener::class)
data class CalculationStatus(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private val status: Status? = null,

    @Column(name = "operation_uid")
    private val operationUid: String? = null,

    @Column(name = "calculation_id", updatable = false, insertable = false)
    private val calculationId: Long? = null,

    @ManyToOne
    @JoinColumn(name = "calculation_id", foreignKey = ForeignKey(name = "fk_calculation_status_calculation_id"))
    private val calculation: Calculation? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private val createdDate: LocalDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalculationStatus

        if (status != other.status) return false
        if (operationUid != other.operationUid) return false
        if (calculationId != other.calculationId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status?.hashCode() ?: 0
        result = 31 * result + (operationUid?.hashCode() ?: 0)
        result = 31 * result + (calculationId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CalculationStatus(id=$id, status=$status, operationUid=$operationUid, calculationId=$calculationId, createdDate=$createdDate)"
    }
}
