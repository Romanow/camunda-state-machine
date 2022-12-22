package ru.romanow.camunda.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.camunda.domain.enums.CalculationType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "calculation")
@EntityListeners(AuditingEntityListener::class)
data class Calculation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "uid", nullable = false, updatable = false)
    val uid: UUID? = null,

    @Column(name = "name", nullable = false)
    val name: String? = null,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    val type: CalculationType? = null,

    @OneToMany(mappedBy = "calculation")
    val statuses: List<CalculationStatus>? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    val createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    val modifiedDate: LocalDateTime? = null,
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
        return "Calculation(id=$id, uid=$uid, name=$name, description=$description, type=$type, createdDate=$createdDate, modifiedDate=$modifiedDate)"
    }
}