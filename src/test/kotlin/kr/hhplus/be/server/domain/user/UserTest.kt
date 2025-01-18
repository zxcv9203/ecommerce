package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.UserFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserTest {
    @Nested
    @DisplayName("잔액 충전")
    inner class Charge {
        @Test
        @DisplayName("[성공] 정상적인 금액으로 충전")
        fun test_charge_success() {
            val user = UserFixture.create()
            val want = 10_000L

            user.charge(want)

            assertThat(user.balance).isEqualTo(want)
        }

        @ParameterizedTest(name = "{0}원으로 충전")
        @ValueSource(longs = [9_999L, 0L, -1000L])
        @DisplayName("[실패] 충전 금액이 최소 금액 미만일 경우 BusinessException 발생")
        fun test_charge_fail_belowMinimum(amount: Long) {
            val user = UserFixture.create()

            assertThatThrownBy { user.charge(amount) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USER_BALANCE_BELOW_MINIMUM)
        }

        @ParameterizedTest(name = "{0}원으로 충전")
        @ValueSource(longs = [10_000_000L])
        @DisplayName("[실패] 충전 후 잔액이 최대 잔액을 초과할 경우 BusinessException 발생")
        fun test_charge_fail_exceedsMaximum(amount: Long) {
            val user = UserFixture.create()

            assertThatThrownBy { user.charge(amount) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USER_BALANCE_EXCEEDS_LIMIT)
        }
    }

    @Nested
    @DisplayName("잔액 사용")
    inner class Use {
        @Test
        @DisplayName("[성공] 정상적인 금액으로 사용")
        fun test_use_success() {
            val user = UserFixture.create(balance = 10_000L)
            val want = 5_000L

            user.use(want)

            assertThat(user.balance).isEqualTo(5_000L)
        }

        @Test
        @DisplayName("[실패] 사용 금액이 잔액을 초과할 경우 BusinessException 발생")
        fun test_use_fail_exceedsBalance() {
            val user = UserFixture.create(balance = 10_000L)
            val want = 15_000L

            assertThatThrownBy { user.use(want) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.INSUFFICIENT_BALANCE)
        }
    }
}
