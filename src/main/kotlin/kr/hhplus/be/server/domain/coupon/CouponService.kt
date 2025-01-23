package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.application.coupon.info.CouponInfo
import kr.hhplus.be.server.application.coupon.command.FindUserCouponCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.user.User
import org.springframework.data.domain.Slice
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponPolicyRepository: CouponPolicyRepository,
) {
    @Transactional
    fun issue(
        user: User,
        couponPolicyId: Long,
    ) {
        val couponPolicy =
            couponPolicyRepository.findById(couponPolicyId)
                ?: throw BusinessException(ErrorCode.COUPON_NOT_FOUND)
        couponRepository
            .findByUserIdAndPolicyId(user.id, couponPolicyId)
            .checkAlreadyIssue()
        val coupon = couponPolicy.issue(user)
        couponRepository.save(coupon)
    }

    fun findAllByUserId(command: FindUserCouponCommand): Slice<CouponInfo> =
        couponRepository.findAllByUserId(command.userId, command.pageable)

    fun getOrderableCoupon(
        couponId: Long,
        user: User,
    ): Coupon =
        couponRepository
            .findById(couponId)
            ?.also { it.ensureOwner(user) }
            ?.also { it.ensureUsableStatus() }
            ?: throw BusinessException(ErrorCode.COUPON_NOT_FOUND)

    @Transactional
    fun reserve(
        coupon: Coupon,
        order: Order,
    ) {
        coupon.reserve(order)
        try {
            couponRepository.save(coupon)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw BusinessException(ErrorCode.COUPON_USE_FAIL)
        }
    }

    fun findByOrderId(orderId: Long): Coupon? = couponRepository.findByOrderId(orderId)

    @Transactional
    fun use(coupon: Coupon) {
        coupon.use()
        try {
            couponRepository.save(coupon)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw BusinessException(ErrorCode.COUPON_USE_FAIL)
        }
    }
}
