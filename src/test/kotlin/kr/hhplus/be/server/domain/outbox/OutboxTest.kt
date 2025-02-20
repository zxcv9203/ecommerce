package kr.hhplus.be.server.domain.outbox

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OutboxTest {
    @Nested
    @DisplayName("재시도 처리를 진행한다.")
    inner class ProcessRetry {
        @Test
        @DisplayName("[성공] 재시도 최대 횟수를 초과하지 않는 경우 카운트를 1증가 시킨다.")
        fun test_incrementRetryCount() {
            val outbox =
                Outbox(
                    aggregateId = 1L,
                    eventType = OutboxEventType.PAYMENT_COMPLETED,
                    retryCount = Outbox.MAX_RETRY_COUNT - 1,
                    payload = "",
                    status = OutboxStatus.PENDING,
                )

            outbox.processRetry()

            assertThat(outbox.retryCount).isEqualTo(Outbox.MAX_RETRY_COUNT)
            assertThat(outbox.status).isEqualTo(OutboxStatus.PENDING)
        }

        @Test
        @DisplayName("[성공] 재시도 최대 횟수를 초과한 경우 상태를 FAILED로 변경한다.")
        fun test_changeStatusToFailed() {
            val outbox =
                Outbox(
                    aggregateId = 1L,
                    eventType = OutboxEventType.PAYMENT_COMPLETED,
                    retryCount = Outbox.MAX_RETRY_COUNT,
                    payload = "",
                    status = OutboxStatus.PENDING,
                )

            outbox.processRetry()

            assertThat(outbox.retryCount).isEqualTo(Outbox.MAX_RETRY_COUNT)
            assertThat(outbox.status).isEqualTo(OutboxStatus.FAILED)
        }
    }
}
