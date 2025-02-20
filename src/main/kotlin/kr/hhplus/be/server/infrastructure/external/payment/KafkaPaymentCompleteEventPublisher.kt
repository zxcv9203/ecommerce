package kr.hhplus.be.server.infrastructure.external.payment

import kr.hhplus.be.server.application.payment.PaymentCompleteEventPublisher
import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent
import kr.hhplus.be.server.common.util.toJson
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
@Primary
class KafkaPaymentCompleteEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : PaymentCompleteEventPublisher {
    override fun send(event: PaymentCompletedEvent) {
        kafkaTemplate.send("payment-complete", event.toJson())
    }
}
