package kr.hhplus.be.server.domain.coupon

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SyncCouponIssueServiceTest {
    @InjectMockKs
    private lateinit var couponService: SyncCouponIssueService

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
}
