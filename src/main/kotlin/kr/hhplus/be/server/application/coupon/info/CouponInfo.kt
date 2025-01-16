package kr.hhplus.be.server.application.coupon.info

import kr.hhplus.be.server.domain.coupon.CouponDiscountType
import kr.hhplus.be.server.domain.coupon.CouponStatus

data class CouponInfo(
    val id: Long,
    val policyId: Long,
    val name: String,
    val description: String,
    val discountType: CouponDiscountType,
    val discountValue: Long,
    val status: CouponStatus,
)

data class CouponsInfo(
    val coupons: List<CouponInfo>,
    val hasNext: Boolean,
)
