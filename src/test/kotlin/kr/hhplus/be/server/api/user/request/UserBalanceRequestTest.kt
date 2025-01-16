package kr.hhplus.be.server.api.user.request

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserBalanceRequestTest {
    @Nested
    @DisplayName("객체 생성")
    inner class Init {
        @Test
        @DisplayName("[실패] amount가 null인 경우 BusinessException 발생")
        fun testFailWhenAmountIsNull() {
            val amount: Long? = null

            assertThatThrownBy { UserBalanceRequest(amount) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USER_BALANCE_BELOW_MINIMUM)
        }
    }

    @Nested
    @DisplayName("command 객체 변환")
    inner class ToCommand {
        @Test
        @DisplayName("[실패] 유저 아이디랑 인증 아이디가 다를 때 FORBIDDEN 에러 발생")
        fun testFailWhenUserIdAndAuthIdDiff() {
            val userBalanceRequest = UserBalanceRequest(1000)

            assertThatThrownBy { userBalanceRequest.toCommand(1, 2) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.FORBIDDEN)
        }
    }
}