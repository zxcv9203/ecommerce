package kr.hhplus.be.server.domain.coupon

interface CouponRepository {
    fun save(coupon: Coupon): Coupon

    fun findByUserIdAndPolicyId(
        userId: Long,
        couponPolicyId: Long,
    ): Coupon?
}
