package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.application.coupon.info.CouponsInfo
import kr.hhplus.be.server.application.coupon.command.FindUserCouponCommand
import kr.hhplus.be.server.application.coupon.command.IssueCouponCommand
import kr.hhplus.be.server.domain.coupon.CouponService
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class CouponUseCase(
    private val couponService: CouponService,
    private val userService: UserService,
) {
    fun issue(command: IssueCouponCommand) {
        val user = userService.getById(command.userId)
        couponService.issue(user, command.couponPolicyId)
    }

    fun findAllByUserId(command: FindUserCouponCommand): CouponsInfo {
        userService.getById(command.userId)

        return couponService
            .findAllByUserId(command)
            .let { CouponsInfo(it.content, it.hasNext()) }
    }
}
