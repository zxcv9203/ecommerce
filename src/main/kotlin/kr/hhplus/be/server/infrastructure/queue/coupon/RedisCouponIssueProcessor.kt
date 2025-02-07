package kr.hhplus.be.server.infrastructure.queue.coupon

import kr.hhplus.be.server.domain.coupon.CouponIssueProcessor
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCouponIssueProcessor(
    private val redisTemplate: StringRedisTemplate,
) : CouponIssueProcessor {
    override fun pushCouponIssueQueue(
        userId: Long,
        couponPolicyId: Long,
    ) {
        val timestamp = System.currentTimeMillis().toDouble()
        redisTemplate.opsForZSet().addIfAbsent("$COUPON_QUEUE_KEY$couponPolicyId", userId.toString(), timestamp)
    }

    override fun popCouponIssueQueue(
        size: Long,
        couponPolicyId: Long,
    ): List<Long> =
        redisTemplate
            .opsForZSet()
            .popMin("$COUPON_QUEUE_KEY$couponPolicyId", size)
            ?.mapNotNull { it.value?.toLongOrNull() }
            ?: emptyList()

    override fun isAlreadyIssued(
        userId: Long,
        couponPolicyId: Long,
    ): Boolean {
        val issuedKey = "$ISSUED_USERS_KEY$couponPolicyId"
        return redisTemplate.opsForSet().isMember(issuedKey, userId.toString()) ?: false
    }

    override fun markAsIssued(
        userId: Long,
        couponPolicyId: Long,
    ) {
        val issuedKey = "$ISSUED_USERS_KEY$couponPolicyId"
        redisTemplate.opsForSet().add(issuedKey, userId.toString())
    }

    companion object {
        private const val COUPON_QUEUE_KEY = "coupon_queue-"
        private const val ISSUED_USERS_KEY = "issued_users-"
    }
}
