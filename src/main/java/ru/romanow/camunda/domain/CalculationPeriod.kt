package ru.romanow.camunda.domain

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "calculation_period")
data class CalculationPeriod(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "serial_number", nullable = false)
    var serialNumber: Int? = null,

    @Column(name = "mark", length = 5, nullable = false)
    var mark: String? = null,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate? = null,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calculation_id", foreignKey = ForeignKey(name = "fk_calculation_period_on_calculation_id"))
    var calculation: Calculation? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalculationPeriod

        if (serialNumber != other.serialNumber) return false
        if (mark != other.mark) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serialNumber ?: 0
        result = 31 * result + (mark?.hashCode() ?: 0)
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (endDate?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CalculationPeriodEntity(id=$id, serialNumber=$serialNumber, mark=$mark, startDate=$startDate, endDate=$endDate)"
    }
}