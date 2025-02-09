package kr.hhplus.be.server.infrastructure.queue.coupon

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations.TypedTuple

@ExtendWith(MockKExtension::class)
class RedisCouponIssueProcessorTest {
    @InjectMockKs
    private lateinit var redisCouponIssueProcessor: RedisCouponIssueProcessor

    @MockK
    private lateinit var redisTemplate: StringRedisTemplate

    @Nested
    @DisplayName("쿠폰 발급 요청을 가져옵니다.")
    inner class PopCouponIssueQueue {
        @Test
        @DisplayName("[성공] 쿠폰 발급 요청을 조회하고 요청 목록에서 제거합니다.")
        fun test_couponPop() {
            val couponPolicyId = 1L
            val size = 1L
            val want = listOf(1L)

            every { redisTemplate.opsForZSet().popMin("coupon_queue-1", size) } returns setOf(TypedTuple.of("1", 100.0))
            val got = redisCouponIssueProcessor.popCouponIssueQueue(size, couponPolicyId)

            assertThat(got).usingRecursiveComparison().isEqualTo(want)
        }

        @Test
        @DisplayName("[성공] 쿠폰 발급 요청이 없는 경우 빈 리스트를 반환합니다.")
        fun test_emptyRequest() {
            val couponPolicyId = 1L
            val size = 1L
            val want = emptyList<Long>()

            every { redisTemplate.opsForZSet().popMin("coupon_queue-1", size) } returns null
            val got = redisCouponIssueProcessor.popCouponIssueQueue(size, couponPolicyId)

            assertThat(got).usingRecursiveComparison().isEqualTo(want)
        }
    }
}
