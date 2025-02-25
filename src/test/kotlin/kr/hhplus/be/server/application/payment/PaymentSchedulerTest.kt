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
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

class PaymentSchedulerTest : IntegrationTest() {
    @Autowired
    private lateinit var paymentScheduler: PaymentScheduler

    @Autowired
    private lateinit var outboxRepository: DataJpaOutboxRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        val outbox =
            Outbox(
                aggregateId = 1L,
                eventType = OutboxEventType.PAYMENT_COMPLETED,
                status = OutboxStatus.PENDING,
                payload = "{\"amount\": 17000, \"status\": \"SUCCESS\", \"paymentId\": 1}",
            )
        outboxRepository.saveAndFlush(outbox)

        val retryTime = LocalDateTime.now().minusMinutes(6).toString()
        jdbcTemplate.update(
            """
        INSERT INTO outbox (aggregate_id, event_type, status, retry_count, payload, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """,
        ) { ps ->
            ps.setLong(1, 2L)
            ps.setString(2, "PAYMENT_COMPLETED")
            ps.setString(3, "PENDING")
            ps.setInt(4, 0)
            ps.setString(5, """{"amount": 17000, "status": "SUCCESS", "paymentId": 2}""")
            ps.setString(6, retryTime)
            ps.setString(7, retryTime)
        }

        jdbcTemplate.update(
            """
        INSERT INTO outbox (aggregate_id, event_type, status, retry_count, payload, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """,
        ) { ps ->
            ps.setLong(1, 3L)
            ps.setString(2, "PAYMENT_COMPLETED")
            ps.setString(3, "PENDING")
            ps.setInt(4, 3)
            ps.setString(5, """{"amount": 17000, "status": "SUCCESS", "paymentId": 2}""")
            ps.setString(6, retryTime)
            ps.setString(7, retryTime)
        }
    }

    @Nested
    @DisplayName("5분이상 처리되지 않은 결제 건에 대해 재처리")
    inner class Republish {
        @Test
        @DisplayName("[성공] 5분이상 PENDING인 건만 재처리 요청하고 재시도 요청이 3회 이상이라면 FAILED로 변경한다.")
        fun test_republish() {
            paymentScheduler.republish()
            Thread.sleep(100)

            val outbox = outboxRepository.findAll()

            assertThat(outbox).hasSize(3)
            val noRepublishOutbox = outbox[0]
            val rePublishOutbox = outbox[1]
            val failedOutbox = outbox[2]

            assertThat(noRepublishOutbox.status).isEqualTo(OutboxStatus.PENDING)
            assertThat(noRepublishOutbox.retryCount).isEqualTo(0)

            assertThat(rePublishOutbox.status).isEqualTo(OutboxStatus.PROCESSED)
            assertThat(rePublishOutbox.retryCount).isEqualTo(1)

            assertThat(failedOutbox.status).isEqualTo(OutboxStatus.FAILED)
            assertThat(failedOutbox.retryCount).isEqualTo(3)
        }
    }
}
