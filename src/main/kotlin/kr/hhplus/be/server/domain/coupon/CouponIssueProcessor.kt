package kr.hhplus.be.server.domain.coupon

interface CouponIssueProcessor {
    fun pushCouponIssueQueue(
        userId: Long,
        couponPolicyId: Long,
    )

    fun popCouponIssueQueue(
        size: Long,
        couponPolicyId: Long,
    ): List<Long>

    fun isAlreadyIssued(
        userId: Long,
        couponPolicyId: Long,
    ): Boolean

    fun markAsIssued(
        userId: Long,
        couponPolicyId: Long,
    )
}
