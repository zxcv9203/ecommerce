package kr.hhplus.be.server.infrastructure.persistence.outbox

import kr.hhplus.be.server.domain.outbox.Outbox
import kr.hhplus.be.server.domain.outbox.OutboxEventType
import kr.hhplus.be.server.domain.outbox.OutboxRepository
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class JpaOutboxRepository(
    private val dataJpaOutboxRepository: DataJpaOutboxRepository,
) : OutboxRepository {
    override fun save(entity: Outbox): Outbox = dataJpaOutboxRepository.save(entity)

    override fun saveAll(entities: List<Outbox>): List<Outbox> = dataJpaOutboxRepository.saveAll(entities)

    override fun findByAggregateIdAndStatusWithLock(
        aggregateId: Long,
        status: OutboxStatus,
    ): Outbox? = dataJpaOutboxRepository.findByAggregateId(aggregateId, status)

    override fun findAllByStatusAndEventTypeWithLockBefore(
        status: OutboxStatus,
        type: OutboxEventType,
        time: LocalDateTime,
        size: Int,
    ): List<Outbox> =
        dataJpaOutboxRepository.findAllByStatusAndEventTypeWithLockBefore(
            status,
            type,
            time,
            size,
        )
}
