package kr.hhplus.be.server.domain.coupon

interface CouponPolicyRepository {
    fun findByIdWithLock(id: Long): CouponPolicy?
}
