package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.application.user.command.UseBalanceCommand
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaBalanceHistoryRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class IntegrationUserServiceTest : IntegrationTest() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaBalanceHistoryRepository: DataJpaBalanceHistoryRepository

    private val defaultBalance = 10000L

    @BeforeEach
    fun setUp() {
        val user = UserFixture.create(id = 0L, balance = defaultBalance)
        dataJpaUserRepository.saveAndFlush(user)
    }

    @Nested
    @DisplayName("사용자 잔액 충전 동시성 테스트")
    inner class ChargeBalance {
        @Test
        @DisplayName("[동시성] 동시에 사용자가 10번 잔액을 충전할 때 잔액 충전에 성공한 만큼 잔액이 증가해야 한다.")
        fun chargeBalanceConcurrentTest() {
            val amount = 10000L
            val command = ChargeBalanceCommand(1, amount)

            val result =
                ConcurrentTestHelper.executeAsyncTasks(10) {
                    userService.chargeBalance(command)
                }

            val successCount = result.count { it }
            val updatedUser = dataJpaUserRepository.findByIdOrNull(1) ?: throw RuntimeException("User not found")
            val balanceHistories = dataJpaBalanceHistoryRepository.findAll()

            assertThat(updatedUser.balance).isEqualTo(defaultBalance + successCount * amount)
            assertThat(balanceHistories.size).isEqualTo(successCount)
            assertThat(balanceHistories.all { it.amount == amount }).isTrue()
            assertThat(balanceHistories.all { it.user.id == updatedUser.id }).isTrue()
        }
    }

    @Nested
    @DisplayName("사용자 잔액 사용 동시성 테스트")
    inner class UseBalance {
        @Nested
        @DisplayName("[동시성] 동시에 사용자가 10번 잔액을 사용할 때 잔액 사용에 성공한 만큼 잔액이 감소해야 한다.")
        inner class UseBalanceConcurrentTest {
            @Test
            fun useBalanceConcurrentTest() {
                val amount = 1000L
                val command = UseBalanceCommand(1, amount)

                val result =
                    ConcurrentTestHelper.executeAsyncTasks(10) {
                        userService.useBalance(command)
                    }

                val successCount = result.count { it }
                val updatedUser = dataJpaUserRepository.findByIdOrNull(1) ?: throw RuntimeException("User not found")
                val balanceHistories = dataJpaBalanceHistoryRepository.findAll()

                assertThat(updatedUser.balance).isEqualTo(defaultBalance - successCount * amount)
                assertThat(balanceHistories.size).isEqualTo(successCount)
                assertThat(balanceHistories.all { it.amount == amount }).isTrue()
                assertThat(balanceHistories.all { it.user.id == updatedUser.id }).isTrue()
            }
        }
    }

    @Nested
    @DisplayName("사용자 잔액 충전 / 사용 동시 사용 동시성 테스트")
    inner class ChargeAndUseBalance {
        @Test
        @DisplayName("[동시성] 동시에 사용자가 10번 잔액을 충전하고 사용할 때 잔액이 증가하고 감소해야 한다.")
        fun chargeAndUseBalanceConcurrentTest() {
            val chargeAmount = 10000L
            val useAmount = 1000L
            val chargeCommand = ChargeBalanceCommand(1, chargeAmount)
            val useCommand = UseBalanceCommand(1, useAmount)
            val result =
                ConcurrentTestHelper.executeAsyncTasksByMultiTask(10) {
                    val isChargeSuccess =
                        try {
                            userService.chargeBalance(chargeCommand)
                            true
                        } catch (e: Exception) {
                            false
                        }

                    val isUseSuccess =
                        try {
                            userService.useBalance(useCommand)
                            true
                        } catch (e: Exception) {
                            false
                        }

                    isChargeSuccess to isUseSuccess
                }

            val successfulChargeCount = result.count { it.first }
            val successfulUseCount = result.count { it.second }
            val updatedUser = dataJpaUserRepository.findByIdOrNull(1L) ?: throw RuntimeException("User not found")
            val balanceHistories = dataJpaBalanceHistoryRepository.findAll()
            assertThat(updatedUser.balance).isEqualTo(
                defaultBalance + (successfulChargeCount * chargeAmount) - (successfulUseCount * useAmount),
            )
            assertThat(balanceHistories.size).isEqualTo(successfulChargeCount + successfulUseCount)
            assertThat(balanceHistories.count { it.amount == chargeAmount }).isEqualTo(successfulChargeCount)
            assertThat(balanceHistories.count { it.amount == useAmount }).isEqualTo(successfulUseCount)
            assertThat(balanceHistories.all { it.user.id == updatedUser.id }).isTrue()
        }
    }
}
