package kr.hhplus.be.server.api.coupon.request

import kr.hhplus.be.server.application.coupon.command.IssueCouponCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException

data class IssueCouponRequest(
    val couponPolicyId: Long,
) {
    fun toCommand(
        userId: Long,
        authenticationId: Long,
    ): IssueCouponCommand {
        if (userId != authenticationId) throw BusinessException(ErrorCode.FORBIDDEN)

        return IssueCouponCommand(
            userId = userId,
            couponPolicyId = couponPolicyId,
        )
    }
}
