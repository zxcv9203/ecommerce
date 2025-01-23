package kr.hhplus.be.server.domain.coupon

interface CouponPolicyRepository {
    fun findById(id: Long): CouponPolicy?
}
