package kr.hhplus.be.server.common.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.constant.AuthConstants
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.model.CustomResponse
import kr.hhplus.be.server.common.util.isNumeric
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class UserAuthenticationInterceptor(
    private val objectMapper: ObjectMapper,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val userIdFromHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (userIdFromHeader.isNullOrBlank() || !userIdFromHeader.isNumeric()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = Charsets.UTF_8.name()

            val errorResponse = CustomResponse.fail(ErrorCode.UNAUTHORIZED)
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
            return false
        }

        request.setAttribute(AuthConstants.AUTH_ID, userIdFromHeader)
        return true
    }
}
