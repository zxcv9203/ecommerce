package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.application.coupon.info.CouponInfo
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaCouponRepository(
    private val dataJpaCouponRepository: DataJpaCouponRepository,
) : CouponRepository {
    override fun save(coupon: Coupon): Coupon = dataJpaCouponRepository.saveAndFlush(coupon)

    override fun findByUserIdAndPolicyId(
        userId: Long,
        couponPolicyId: Long,
    ): Coupon? = dataJpaCouponRepository.findByUserIdAndPolicyId(userId, couponPolicyId)

    override fun findAllByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<CouponInfo> = dataJpaCouponRepository.findAllByUserId(userId, pageable)

    override fun findById(couponId: Long): Coupon? = dataJpaCouponRepository.findByIdOrNull(couponId)

    override fun findByOrderId(orderId: Long): Coupon? = dataJpaCouponRepository.findByOrderId(orderId)
}
