package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.CouponFixture
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}
