package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.application.coupon.info.CouponInfo
import kr.hhplus.be.server.domain.coupon.Coupon
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DataJpaCouponRepository : JpaRepository<Coupon, Long> {
    @Query(
        """
        SELECT new kr.hhplus.be.server.application.coupon.info.CouponInfo(
            c.id,
            c.policy.id,
            p.name,
            p.description,
            p.discountType,
            p.discountAmount,
            c.status
        )
        FROM Coupon c
        JOIN c.policy p
        WHERE c.user.id = :userId
        """,
    )
    fun findAllByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<CouponInfo>

    fun findByUserIdAndPolicyId(
        userId: Long,
        couponPolicyId: Long,
    ): Coupon?

    fun findByOrderId(orderId: Long): Coupon?
}
