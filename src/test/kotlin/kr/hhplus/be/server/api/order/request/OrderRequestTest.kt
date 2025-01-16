package kr.hhplus.be.server.api.order.request

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrderRequestTest {
    @Nested
    @DisplayName("command 객체 변환")
    inner class ToCommand {
        @Test
        @DisplayName("[실패] 유저 아이디랑 인증 아이디가 다를 때 FORBIDDEN 에러 발생")
        fun userIdAuthIdDiffTest() {
            val orderRequest = OrderRequest(1, emptyList(), null)

            assertThatThrownBy { orderRequest.toCommand(2) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.FORBIDDEN)
        }
    }
}
