package kr.hhplus.be.server.application.coupon.command

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable

class FindUserCouponCommandTest {
    @Nested
    @DisplayName("command 객체 생성")
    inner class Of {
        @Test
        @DisplayName("[실패] 유저 아이디랑 인증 아이디가 다를 때 FORBIDDEN 에러 발생")
        fun userIdAuthIdDiffTest() {
            assertThatThrownBy { FindUserCouponCommand.of(1, 2, Pageable.ofSize(10)) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.FORBIDDEN)
        }
    }
}
