package kr.hhplus.be.server.api.coupon.request

import kr.hhplus.be.server.application.coupon.command.IssueCouponCommand

data class IssueCouponRequest(
    val couponPolicyId: Long,
) {
    fun toCommand(userId: Long) =
        IssueCouponCommand(
            userId = userId,
            couponPolicyId = couponPolicyId,
        )
}
