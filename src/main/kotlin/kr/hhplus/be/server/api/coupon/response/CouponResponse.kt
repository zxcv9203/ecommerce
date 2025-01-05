package kr.hhplus.be.server.api.coupon.response

import kr.hhplus.be.server.domain.coupon.CouponDiscountType
import kr.hhplus.be.server.domain.coupon.CouponStatus
import java.time.LocalDateTime

data class CouponResponse(
    val id: Long,
    val policyId: Long,
    val name: String,
    val description: String,
    val discountType: CouponDiscountType,
    val discountValue: Int,
    val status: CouponStatus,
    val issuedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
)

data class CouponsResponse(
    val coupons: List<CouponResponse>,
    val hasNext: Boolean,
)
