package kr.hhplus.be.server.application.payment

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent
import kr.hhplus.be.server.domain.outbox.OutboxEventType
import kr.hhplus.be.server.domain.outbox.OutboxRepository
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentScheduler(
    private val paymentCompleteEventPublisher: PaymentCompleteEventPublisher,
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    @Scheduled(fixedRate = 50000)
    @Transactional
    fun republish() {
        val now = LocalDateTime.now()
        outboxRepository
            .findAllByStatusAndEventTypeWithLockBefore(
                OutboxStatus.PENDING,
                OutboxEventType.PAYMENT_COMPLETED,
                now.minusMinutes(5),
                FETCH_SIZE,
            ).onEach { it.processRetry() }
            .apply { outboxRepository.saveAll(this) }
            .filterNot { it.isFailed() }
            .map { it.payload }
            .map { objectMapper.readValue(it, PaymentCompletedEvent::class.java) }
            .forEach { paymentCompleteEventPublisher.send(it) }
    }

    companion object {
        private const val FETCH_SIZE = 20
    }
}
