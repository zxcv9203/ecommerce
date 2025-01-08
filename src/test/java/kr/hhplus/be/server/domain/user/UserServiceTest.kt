package kr.hhplus.be.server.domain.user

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import jakarta.persistence.OptimisticLockException
import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserServiceTest {
    @InjectMockKs
    private lateinit var userService: UserService

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var balanceHistoryRepository: BalanceHistoryRepository

    @Nested
    @DisplayName("ID로 사용자 조회")
    inner class GetById {
        @Test
        @DisplayName("[실패] 사용자가 존재하지 않는 경우 BusinessException 발생")
        fun test_userNotFound() {
            val userId = 1L

            every { userRepository.findById(userId) } returns null

            assertThatThrownBy { userService.getById(userId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    inner class ChargeBalance {
        @Test
        @DisplayName("[성공] 잔액 충전에 성공한다.")
        fun test_chargeBalance() {
            val userId = 1L
            val initialBalance = 50_000L
            val chargeAmount = 10_000L
            val updatedBalance = initialBalance + chargeAmount
            val command = ChargeBalanceCommand(userId, chargeAmount)
            val user = UserFixture.create(balance = initialBalance)
            val savedUser = UserFixture.create(balance = updatedBalance)

            every { userService.getById(userId) } returns user
            every { userRepository.save(user) } returns savedUser
            every { balanceHistoryRepository.save(any()) } returns mockk()

            val got = userService.chargeBalance(command)

            assertThat(got.amount).isEqualTo(updatedBalance)
        }

        @Test
        @DisplayName("[실패] 잔액 충전 중 충돌이 발생하면 BusinessException 발생")
        fun test_chargeBalance_optimisticLockException() {
            val userId = 1L
            val chargeAmount = 10_000L
            val command = ChargeBalanceCommand(userId, chargeAmount)
            val user = UserFixture.create(id = userId)

            every { userService.getById(userId) } returns user
            every { userRepository.save(user) } throws OptimisticLockException()

            assertThatThrownBy { userService.chargeBalance(command) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USER_BALANCE_CHARGE_FAILED)
        }
    }
}
