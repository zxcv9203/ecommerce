package kr.hhplus.be.server.domain.discount

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.coupon.CouponDiscountType
import kr.hhplus.be.server.stub.CouponFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DiscountServiceTest {
    private val discountService = DiscountService()

    @Nested
    @DisplayName("할인 금액 계산")
    inner class GetDiscountedPrice {
        @Test
        @DisplayName("[성공] 고정 금액 할인 금액 계산")
        fun testGetDiscountedPriceSuccess() {
            val couponPolicy =
                CouponFixture.createPolicy(discountType = CouponDiscountType.AMOUNT, discountAmount = 1000)
            val coupon = CouponFixture.create(policy = couponPolicy)
            val price = 10000L

            val discountedPrice = discountService.calculateDiscountedPrice(coupon, price)

            assertThat(discountedPrice).isEqualTo(9000)
        }

        @Test
        @DisplayName("[성공] 퍼센트 할인 금액 계산")
        fun testGetDiscountedPriceSuccessPercent() {
            val couponPolicy =
                CouponFixture.createPolicy(discountType = CouponDiscountType.PERCENT, discountAmount = 10)
            val coupon = CouponFixture.create(policy = couponPolicy)
            val price = 10000L

            val discountedPrice = discountService.calculateDiscountedPrice(coupon, price)

            assertThat(discountedPrice).isEqualTo(9000)
        }

        @ParameterizedTest
        @MethodSource("kr.hhplus.be.server.domain.discount.DiscountServiceTest#provideDiscountAmount")
        @DisplayName("[실패] 할인 금액이 주문 금액보다 크거나 같으면 BusinessException 발생")
        fun testGetDiscountedPriceFail(
            amount: Long,
            discountType: CouponDiscountType,
        ) {
            val couponPolicy =
                CouponFixture.createPolicy(discountType = discountType, discountAmount = amount)
            val coupon = CouponFixture.create(policy = couponPolicy)
            val price = 10000L

            assertThatThrownBy { discountService.calculateDiscountedPrice(coupon, price) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.ORDER_AMOUNT_INVALID)
        }
    }

    companion object {
        @JvmStatic
        fun provideDiscountAmount(): Stream<Arguments> =
            Stream.of(
                Arguments.of(10000L, CouponDiscountType.AMOUNT),
                Arguments.of(101, CouponDiscountType.PERCENT),
            )
    }
}
