package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.domain.user.User

interface CouponIssueService {
    fun issue(
        user: User,
        couponPolicyId: Long,
    )
}
