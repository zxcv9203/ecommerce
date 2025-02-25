package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.domain.outbox.Outbox
import kr.hhplus.be.server.domain.outbox.OutboxEventType
import kr.hhplus.be.server.domain.outbox.OutboxStatus
import kr.hhplus.be.server.infrastructure.persistence.outbox.DataJpaOutboxRepository
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class PaymentCompletedEventListenerTest : IntegrationTest() {
    @Autowired
    private lateinit var paymentCompletedEventListener: PaymentCompletedEventListener

    @Autowired
    private lateinit var outboxRepository: DataJpaOutboxRepository

    @BeforeEach
    fun setUp() {
        val outbox =
            Outbox(
                aggregateId = 1L,
                eventType = OutboxEventType.PAYMENT_COMPLETED,
                status = OutboxStatus.PENDING,
                payload = "{\"amount\": 17000, \"status\": \"SUCCESS\", \"paymentId\": 1}",
            )

        outboxRepository.save(outbox)
    }

    @Nested
    @DisplayName("데이터 플랫폼 결제 완료 이벤트 수신")
    inner class OnPaymentComplete {
        @Test
        @DisplayName("[성공] 결제 완료 이벤트를 수신하면 아웃박스의 상태를 처리 완료(PROCESSED)로 변경한다.")
        fun test_onPaymentComplete() {
            val event = "{\"amount\": 17000, \"status\": \"SUCCESS\", \"paymentId\": 1}"
            paymentCompletedEventListener.onPaymentComplete(event)

            val outbox =
                outboxRepository.findByIdOrNull(1)
                    ?: throw IllegalArgumentException("Outbox not found")

            assertThat(outbox.status).isEqualTo(OutboxStatus.PROCESSED)
        }
    }
}
