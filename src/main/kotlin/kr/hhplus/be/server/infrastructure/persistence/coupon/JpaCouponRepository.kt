package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.api.coupon.response.CouponResponse
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaCouponRepository :
    CouponRepository,
    JpaRepository<Coupon, Long> {
    @Query(
        """
        SELECT new kr.hhplus.be.server.api.coupon.response.CouponResponse(
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
    override fun findAllByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<CouponResponse>
}
