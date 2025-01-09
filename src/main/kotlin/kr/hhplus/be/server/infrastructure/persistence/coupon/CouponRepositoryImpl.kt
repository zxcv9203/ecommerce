package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.api.coupon.response.CouponResponse
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val jpaCouponRepository: JpaCouponRepository,
) : CouponRepository {
    override fun save(coupon: Coupon): Coupon = jpaCouponRepository.saveAndFlush(coupon)

    override fun findByUserIdAndPolicyId(
        userId: Long,
        couponPolicyId: Long,
    ): Coupon? = jpaCouponRepository.findByUserIdAndPolicyId(userId, couponPolicyId)

    override fun findAllByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<CouponResponse> = jpaCouponRepository.findAllByUserId(userId, pageable)

    override fun findById(couponId: Long): Coupon? = jpaCouponRepository.findByIdOrNull(couponId)

    override fun findByOrderId(orderId: Long): Coupon? = jpaCouponRepository.findByOrderId(orderId)
}
