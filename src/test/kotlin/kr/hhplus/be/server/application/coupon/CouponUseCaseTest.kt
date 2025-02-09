package kr.hhplus.be.server.application.coupon

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kr.hhplus.be.server.application.coupon.command.IssueCouponCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.coupon.AsyncCouponIssueService
import kr.hhplus.be.server.domain.coupon.CouponService
import kr.hhplus.be.server.domain.coupon.SyncCouponIssueService
import kr.hhplus.be.server.domain.user.UserService
import kr.hhplus.be.server.stub.UserFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CouponUseCaseTest {
    @InjectMockKs
    private lateinit var couponUseCase: CouponUseCase

    @MockK
    private lateinit var syncCouponIssueService: SyncCouponIssueService

    @MockK
    private lateinit var asyncCouponIssueService: AsyncCouponIssueService

    @MockK
    private lateinit var couponService: CouponService

    @MockK
    private lateinit var userService: UserService

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    inner class Issue {
        @Test
        @DisplayName("[성공] 비동기로 쿠폰 발급에 성공한다.")
        fun testIssueSuccessAsync() {
            val command = IssueCouponCommand(userId = 1L, couponPolicyId = 1L)

            every { userService.getById(command.userId) } returns UserFixture.create(id = command.userId)
            every { asyncCouponIssueService.issue(any(), any()) } returns Unit

            couponUseCase.issue(command)

            verify { asyncCouponIssueService.issue(any(), any()) }
            verify(exactly = 0) { syncCouponIssueService.issue(any(), any()) }
        }

        @Test
        @DisplayName("[실패] 비동기로 쿠폰 발급에 실패하면 동기로 쿠폰 발급에 성공한다.")
        fun testIssueFailAsync() {
            val command = IssueCouponCommand(userId = 1L, couponPolicyId = 1L)

            every { userService.getById(command.userId) } returns UserFixture.create(id = command.userId)
            every {
                asyncCouponIssueService.issue(
                    any(),
                    any(),
                )
            } throws BusinessException(ErrorCode.COUPON_ISSUE_FAILED)
            every { syncCouponIssueService.issue(any(), any()) } returns Unit

            couponUseCase.issue(command)

            verify { asyncCouponIssueService.issue(any(), any()) }
            verify { syncCouponIssueService.issue(any(), any()) }
        }
    }
}
