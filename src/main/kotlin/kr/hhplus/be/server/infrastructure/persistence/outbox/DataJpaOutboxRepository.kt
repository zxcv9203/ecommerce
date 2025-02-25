package kr.hhplus.be.server.infrastructure.persistence.outbox

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.outbox.Outbox
import kr.hhplus.be.server.domain.outbox.OutboxEventType
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface DataJpaOutboxRepository : JpaRepository<Outbox, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT o
        FROM Outbox o
        WHERE o.status = :status
        AND o.eventType = :type
        AND o.createdAt < :time
        ORDER BY o.id
        LIMIT :size
    """,
    )
    fun findAllByStatusAndEventTypeWithLockBefore(
        status: OutboxStatus,
        type: OutboxEventType,
        time: LocalDateTime,
        size: Int,
    ): List<Outbox>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT o
        FROM Outbox o
        WHERE o.aggregateId = :aggregateId
        AND o.status = :status
    """,
    )
    fun findByAggregateId(
        aggregateId: Long,
        status: OutboxStatus,
    ): Outbox?
}
