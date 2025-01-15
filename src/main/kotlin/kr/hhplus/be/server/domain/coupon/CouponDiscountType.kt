package kr.hhplus.be.server.domain.coupon

enum class CouponDiscountType(
    val discount: (price: Long, discountAmount: Long) -> Long,
) {
    PERCENT(
        { price, discountAmount -> price - (price * discountAmount / 100) },
    ),
    AMOUNT(
        { price, discountAmount -> price - discountAmount },
    ),
}
