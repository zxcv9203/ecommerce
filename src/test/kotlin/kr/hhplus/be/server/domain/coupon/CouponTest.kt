package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.OrderFixture
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CouponTest {
    @Nested
    @DisplayName("쿠폰이 존재하는 경우 예외 발생")
    inner class CheckAlreadyIssue {
        @Test
        @DisplayName("[실패] 쿠폰이 존재하면 BusinessException 발생")
        fun testCheckAlreadyIssueFail() {
            val coupon = CouponFixture.create()
            assertThatThrownBy { coupon.checkAlreadyIssue() }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_ALREADY_ISSUED)
        }
    }

    @Nested
    @DisplayName("쿠폰 소유자 여부 확인")
    inner class EnsureCoupon {
        @Test
        @DisplayName("[실패] 쿠폰 소유자가 아닌 경우 BusinessException 발생")
        fun testEnsureCouponFail() {
            val user1 = UserFixture.create(id = 1)
            val user2 = UserFixture.create(id = 2)
            val coupon = CouponFixture.create(user = user1)

            assertThatThrownBy { coupon.ensureOwner(user2) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_OWNER_MISMATCH)
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 가능 상태인지 확인")
    inner class EnsureUsableStatus {
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = ["USED", "RESERVED", "CANCELLED"])
        @DisplayName("[실패] 쿠폰이 사용 불가능한 상태라면 BusinessException 발생")
        fun testEnsureUsableFail(status: String) {
            val couponStatus = CouponStatus.valueOf(status)
            val coupon = CouponFixture.create(status = couponStatus)

            assertThatThrownBy { coupon.ensureUsableStatus() }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_ALREADY_USED)
        }
    }

    @Nested
    @DisplayName("쿠폰 예약")
    inner class Reserve {
        @Test
        @DisplayName("[성공] 쿠폰을 예약 상태로 변경합니다.")
        fun testReserveSuccess() {
            val order = OrderFixture.create()
            val coupon = CouponFixture.create(status = CouponStatus.ACTIVE)

            coupon.reserve(order)

            assertThat(coupon.status).isEqualTo(CouponStatus.RESERVED)
            assertThat(coupon.order).isEqualTo(order)
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = ["USED", "RESERVED", "CANCELLED"])
        @DisplayName("[실패] 쿠폰이 사용 불가능한 상태라면 BusinessException 발생")
        fun testReserveFail(status: String) {
            val couponStatus = CouponStatus.valueOf(status)
            val order = OrderFixture.create()
            val coupon = CouponFixture.create(status = couponStatus)

            assertThatThrownBy { coupon.reserve(order) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_ALREADY_USED)
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    inner class Use {
        @Test
        @DisplayName("[성공] 쿠폰을 사용 상태로 변경합니다.")
        fun testUseSuccess() {
            val coupon = CouponFixture.create(status = CouponStatus.RESERVED)

            coupon.use()

            assertThat(coupon.status).isEqualTo(CouponStatus.USED)
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = ["ACTIVE", "USED", "CANCELLED"])
        @DisplayName("[실패] 쿠폰이 사용 불가능한 상태라면 BusinessException 발생")
        fun testUseFail(status: String) {
            val couponStatus = CouponStatus.valueOf(status)
            val coupon = CouponFixture.create(status = couponStatus)

            assertThatThrownBy { coupon.use() }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_NOT_ORDER_RESERVED)
        }
    }
}
