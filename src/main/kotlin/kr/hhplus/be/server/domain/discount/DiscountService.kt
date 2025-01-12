package kr.hhplus.be.server.domain.discount

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.coupon.Coupon
import org.springframework.stereotype.Service

@Service
class DiscountService {
    fun calculateDiscountedPrice(
        coupon: Coupon?,
        price: Long,
    ): Long {
        if (coupon == null) return price

        val discountPrice =
            coupon
                .getDiscountType()
                .discount(price, coupon.getDiscountAmount())

        if (discountPrice <= 0) {
            throw BusinessException(ErrorCode.ORDER_AMOUNT_INVALID)
        }
        return discountPrice
    }
}
