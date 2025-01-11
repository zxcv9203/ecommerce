package kr.hhplus.be.server.application.coupon.command

import org.springframework.data.domain.Pageable

data class FindUserCouponCommand(
    val userId: Long,
    val pageable: Pageable,
)
