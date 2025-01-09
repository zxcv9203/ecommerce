package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.api.coupon.response.CouponResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface CouponRepository {
    fun save(coupon: Coupon): Coupon

    fun findByUserIdAndPolicyId(
        userId: Long,
        couponPolicyId: Long,
    ): Coupon?

    fun findAllByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<CouponResponse>

    fun findById(couponId: Long): Coupon?
}
