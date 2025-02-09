package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.domain.user.User
import org.springframework.stereotype.Service

@Service
class AsyncCouponIssueService(
    private val couponIssueProcessor: CouponIssueProcessor,
) : CouponIssueService {
    override fun issue(
        user: User,
        couponPolicyId: Long,
    ) {
        couponIssueProcessor.pushCouponIssueQueue(user.id, couponPolicyId)
    }
}
