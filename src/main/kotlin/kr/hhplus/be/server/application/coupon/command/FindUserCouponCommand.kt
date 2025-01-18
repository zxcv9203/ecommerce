package kr.hhplus.be.server.application.coupon.command

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.springframework.data.domain.Pageable

data class FindUserCouponCommand(
    val userId: Long,
    val pageable: Pageable,
) {
    companion object {
        fun of(
            userId: Long,
            authenticationId: Long,
            pageable: Pageable,
        ): FindUserCouponCommand {
            if (userId != authenticationId) throw BusinessException(ErrorCode.FORBIDDEN)
            return FindUserCouponCommand(userId, pageable)
        }
    }
}
