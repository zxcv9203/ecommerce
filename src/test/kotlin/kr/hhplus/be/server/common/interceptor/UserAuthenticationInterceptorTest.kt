package kr.hhplus.be.server.common.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.common.constant.AuthConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class UserAuthenticationInterceptorTest {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val userAuthenticationInterceptor: UserAuthenticationInterceptor =
        UserAuthenticationInterceptor(objectMapper)

    @Nested
    @DisplayName("요청 전 사용자 핸들링")
    inner class PreHandle {
        @Test
        @DisplayName("[성공] 사용자 ID가 숫자로 구성되어 있으면 true 반환")
        fun test() {
            val userId = 1L

            val request =
                MockHttpServletRequest()
                    .apply { addHeader(HttpHeaders.AUTHORIZATION, userId.toString()) }
            val response = MockHttpServletResponse()

            val got = userAuthenticationInterceptor.preHandle(request, response, Any())

            assertThat(got).isTrue()
            val attribute = request.getAttribute(AuthConstants.AUTH_ID)
            assertThat(attribute).isEqualTo(userId.toString())
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "a", "1a"])
        @DisplayName("[실패] 숫자로 구성된 아이디가 아닌 경우 false 반환")
        fun testFail(userId: String) {
            val request =
                MockHttpServletRequest()
                    .apply { addHeader(HttpHeaders.AUTHORIZATION, userId) }
            val response = MockHttpServletResponse()

            val got = userAuthenticationInterceptor.preHandle(request, response, Any())

            assertThat(got).isFalse()
        }
    }
}
