package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponPolicyTest {
    @Nested
    @DisplayName("쿠폰 발급")
    inner class Issue {
        @Test
        @DisplayName("[성공] 새로운 쿠폰을 생성하고 현재 발급 수를 1 증가시킵니다.")
        fun testIssueSuccess() {
            val couponPolicy = CouponFixture.createPolicy()
            val user = UserFixture.create()
            val wantCurrentCount = couponPolicy.currentCount + 1

            val got = couponPolicy.issue(user)

            assertThat(couponPolicy.currentCount).isEqualTo(wantCurrentCount)
            assertThat(got.user).isEqualTo(user)
        }

        @Test
        @DisplayName("[실패] 현재 시간이 쿠폰 발급 가능 기간에 도달하지 않으면 BusinessException 발생")
        fun testIssueFailBeforeStartTime() {
            val couponPolicy =
                CouponFixture.createPolicy(
                    startTime = LocalDateTime.now().plusDays(1),
                    endTime = LocalDateTime.now().plusDays(2),
                )
            val user = UserFixture.create()

            assertThatThrownBy { couponPolicy.issue(user) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_ISSUE_NOT_STARTED)
        }

        @Test
        @DisplayName("[실패] 쿠폰 발급 가능 기간이 지났으면 BusinessException 발생")
        fun testIssueFailAfterEndTime() {
            val couponPolicy =
                CouponFixture.createPolicy(
                    startTime = LocalDateTime.now().minusDays(2),
                    endTime = LocalDateTime.now().minusDays(1),
                )
            val user = UserFixture.create()

            assertThatThrownBy { couponPolicy.issue(user) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_ISSUE_EXPIRED)
        }

        @Test
        @DisplayName("[실패] 쿠폰 발급 가능 수량이 초과되면 BusinessException 발생")
        fun testIssueFailOutOfCount() {
            val couponPolicy = CouponFixture.createPolicy(totalCount = 1, currentCount = 1)
            val user = UserFixture.create()

            assertThatThrownBy { couponPolicy.issue(user) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_OUT_OF_COUNT)
        }
    }


}
