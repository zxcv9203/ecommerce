package kr.hhplus.be.server.domain.outbox

import java.time.LocalDateTime

interface OutboxRepository {
    fun save(entity: Outbox): Outbox

    fun saveAll(entities: List<Outbox>): List<Outbox>

    fun findByAggregateIdAndStatusWithLock(
        aggregateId: Long,
        status: OutboxStatus,
    ): Outbox?

    fun findAllByStatusAndEventTypeWithLockBefore(
        status: OutboxStatus,
        type: OutboxEventType,
        time: LocalDateTime,
        size: Int,
    ): List<Outbox>
}
