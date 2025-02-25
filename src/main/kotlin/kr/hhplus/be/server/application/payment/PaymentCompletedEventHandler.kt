package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent
import kr.hhplus.be.server.common.util.toJson
import kr.hhplus.be.server.domain.outbox.Outbox
import kr.hhplus.be.server.domain.outbox.OutboxEventType
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import kr.hhplus.be.server.domain.outbox.OutboxRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentCompletedEventHandler(
    private val outboxRepository: OutboxRepository,
    private val eventPublisher: PaymentCompleteEventPublisher,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBefore(event: PaymentCompletedEvent) {
        outboxRepository.save(
            Outbox(
                aggregateId = event.paymentId,
                status = OutboxStatus.PENDING,
                eventType = OutboxEventType.PAYMENT_COMPLETED,
                payload = event.toJson(),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfter(event: PaymentCompletedEvent) {
        eventPublisher.send(event)
    }
}
