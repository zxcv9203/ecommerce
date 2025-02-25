package kr.hhplus.be.server.application.payment

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent
import kr.hhplus.be.server.domain.outbox.OutboxRepository
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentCompletedEventListener(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["payment-complete"], groupId = "payment")
    @Transactional
    fun onPaymentComplete(event: String) {
        val parseEvent = objectMapper.readValue(event, PaymentCompletedEvent::class.java)
        val outbox =
            outboxRepository.findByAggregateIdAndStatusWithLock(parseEvent.paymentId, OutboxStatus.PENDING)
                ?: throw IllegalArgumentException("outbox not found: ${parseEvent.paymentId}")
        outbox.consume()
        outboxRepository.save(outbox)
        log.info("결제 플랫폼 처리 성공: $event")
    }
}
