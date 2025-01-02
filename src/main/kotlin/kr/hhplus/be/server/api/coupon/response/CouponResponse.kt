package kr.hhplus.be.server.api.coupon.response

import java.time.LocalDateTime

data class CouponResponse(
    val id: Long,
    val policyId: Long,
    val discountType: String,
    val discountValue: Int,
    val status: String,
    val issuedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
)

data class CouponsResponse(
    val coupons: List<CouponResponse>,
    val hasNext: Boolean,
)
