package kr.hhplus.be.server.domain.user

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
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
}
