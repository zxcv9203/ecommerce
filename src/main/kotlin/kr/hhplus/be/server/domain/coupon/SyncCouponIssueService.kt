package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SyncCouponIssueService(
    private val couponRepository: CouponRepository,
    private val couponPolicyRepository: CouponPolicyRepository,
) : CouponIssueService {
    @Transactional
    override fun issue(
        user: User,
        couponPolicyId: Long,
    ) {
        val couponPolicy =
            couponPolicyRepository.findByIdWithLock(couponPolicyId)
                ?: throw BusinessException(ErrorCode.COUPON_NOT_FOUND)
        couponRepository
            .findByUserIdAndPolicyId(user.id, couponPolicyId)
            .checkAlreadyIssue()
        val coupon = couponPolicy.issue(user)
        couponRepository.save(coupon)
    }
}
