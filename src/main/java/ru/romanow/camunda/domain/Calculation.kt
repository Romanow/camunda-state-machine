package ru.romanow.camunda.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.camunda.domain.enums.CalculationType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "calculation")
@EntityListeners(AuditingEntityListener::class)
data class Calculation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "uid", nullable = false, updatable = false)
    var uid: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDateTime? = null,

    @Column(name = "type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var type: CalculationType? = null,

    @OneToMany(mappedBy = "calculation")
    var statuses: List<CalculationStatus>? = null,

    @Column(name = "product_scenario_uid")
    var productScenarioUid: UUID? = null,

    @Column(name = "transfer_rate_uid")
    var transferRateUid: UUID? = null,

    @Column(name = "macro_uid")
    var macroUid: UUID? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    var modifiedDate: LocalDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Calculation

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Calculation(id=$id, uid=$uid, name=$name, description=$description, startDate=$startDate, type=$type, productScenarioUid=$productScenarioUid, transferRateUid=$transferRateUid, macroUid=$macroUid, createdDate=$createdDate, modifiedDate=$modifiedDate)"
    }
}