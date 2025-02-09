package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.coupon.CouponIssueProcessor
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository
import kr.hhplus.be.server.domain.coupon.SyncCouponIssueService
import kr.hhplus.be.server.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CouponIssueScheduler(
    private val syncCouponIssueService: SyncCouponIssueService,
    private val userService: UserService,
    private val couponPolicyRepository: CouponPolicyRepository,
    private val couponIssueProcessor: CouponIssueProcessor,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = COUPON_ISSUE_INTERVAL)
    fun issueCoupon() {
        val couponPolicies = couponPolicyRepository.findIssuableCouponPolicy()

        couponPolicies.forEach { couponPolicy ->
            val userIds =
                couponIssueProcessor.popCouponIssueQueue(QUERY_COUNT, couponPolicy.id)
            userIds.forEach { userId ->
                runCatching {
                    if (couponIssueProcessor.isAlreadyIssued(userId, couponPolicy.id)) {
                        throw BusinessException(ErrorCode.COUPON_ALREADY_ISSUED)
                    }
                    val user = userService.getById(userId)
                    syncCouponIssueService.issue(user, couponPolicy.id)
                    couponIssueProcessor.markAsIssued(userId, couponPolicy.id)
                }.onFailure {
                    log.warn("[CouponIssueScheduler] 쿠폰 발급 실패 (사용자 ID: $userId, 쿠폰 정책 : ${couponPolicy.id}): ${it.message}")
                    // 여기서 사용자에게 알림 처리를 했다고 가정
                }
            }
        }
    }

    companion object {
        private const val COUPON_ISSUE_INTERVAL = 10000L
        private const val QUERY_COUNT = 10L
    }
}
