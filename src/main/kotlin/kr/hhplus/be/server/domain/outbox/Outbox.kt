package kr.hhplus.be.server.domain.outbox

import jakarta.persistence.*
import kr.hhplus.be.server.common.model.BaseEntity

@Entity
@Table(name = "outbox")
class Outbox(
    val aggregateId: Long,
    @Enumerated(EnumType.STRING)
    val eventType: OutboxEventType,
    val payload: String,
    @Enumerated(EnumType.STRING)
    var status: OutboxStatus = OutboxStatus.PENDING,
    var retryCount: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun consume() {
        status = OutboxStatus.PROCESSED
    }

    fun isFailed() = status == OutboxStatus.FAILED

    fun processRetry() {
        if (retryCount >= MAX_RETRY_COUNT) {
            status = OutboxStatus.FAILED
        } else {
            retryCount++
        }
    }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
