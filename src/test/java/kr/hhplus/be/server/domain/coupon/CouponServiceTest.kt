package kr.hhplus.be.server.domain.coupon

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.OrderFixture
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.OptimisticLockingFailureException

@ExtendWith(MockKExtension::class)
class CouponServiceTest {
    @InjectMockKs
    private lateinit var couponService: CouponService

    @MockK
    private lateinit var couponPolicyRepository: CouponPolicyRepository

    @MockK
    private lateinit var couponRepository: CouponRepository

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    inner class Issue {
        @Test
        @DisplayName("[실패] 전달한 쿠폰 정책이 없다면 BusinessException 발생")
        fun testIssueFailWithoutPolicy() {
            val user = UserFixture.create()
            val couponPolicyId = 1L

            every { couponPolicyRepository.findByIdWithLock(couponPolicyId) } returns null

            assertThatThrownBy { couponService.issue(user, couponPolicyId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("주문에 사용가능한 상태의 쿠폰 조회")
    inner class GetOrderableCoupon {
        @Test
        @DisplayName("[실패] 쿠폰이 존재하지 않을 때 BusinessException 발생")
        fun test_fail_when_coupon_not_found() {
            val couponId = 1L
            val user = UserFixture.create()

            every { couponRepository.findById(couponId) } returns null

            assertThatThrownBy { couponService.getOrderableCoupon(couponId, user) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("주문에 쿠폰을 적용한 상태(예약 상태)로 변경")
    inner class Reserve {
        @Test
        @DisplayName("[실패] 쿠폰 상태를 변경 중 충돌이 발생한 경우 BusinessException 발생")
        fun test_fail_when_conflict() {
            val coupon = CouponFixture.create()
            val order = OrderFixture.create()

            every { couponRepository.save(coupon) } throws OptimisticLockingFailureException("")

            assertThatThrownBy { couponService.reserve(coupon, order) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_USE_FAIL)
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    inner class Use {
        @Test
        @DisplayName("[실패] 쿠폰 상태를 변경 중 충돌이 발생한 경우 BusinessException 발생")
        fun test_fail_when_conflict() {
            val coupon = CouponFixture.create(status = CouponStatus.RESERVED)

            every { couponRepository.save(coupon) } throws OptimisticLockingFailureException("")

            assertThatThrownBy { couponService.use(coupon) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.COUPON_USE_FAIL)
        }
    }
}
