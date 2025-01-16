package kr.hhplus.be.server.application.user.command

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GetBalanceCommandTest {
    @Nested
    @DisplayName("command 객체 생성")
    inner class Of {
        @Test
        @DisplayName("[실패] 유저 아이디랑 인증 아이디가 다를 때 FORBIDDEN 에러 발생")
        fun userIdAuthIdDiffTest() {
            assertThatThrownBy { GetBalanceCommand.of(1, 2) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.FORBIDDEN)
        }
    }
}
