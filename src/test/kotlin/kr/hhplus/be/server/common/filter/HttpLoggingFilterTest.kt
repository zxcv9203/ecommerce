package kr.hhplus.be.server.common.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@SpringBootTest
@AutoConfigureMockMvc
class HttpLoggingFilterTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    @DisplayName("HTTP 요청 / 응답 로깅 필터")
    inner class DoFilterInternal {
        @Test
        @DisplayName("[성공] HTTP 요청 / 응답시 로깅을 수행한다.")
        fun successTest() {
            val wantRequestLog =
                "HTTP Request: [Method: POST, URI: /test/log, Headers: Content-Type: application/json;charset=UTF-8, Content-Length: 15, Body: {\"key\":\"value\"}]"
            val wantResponseLog = "HTTP Response: [Status: 200, Body: Request received: {key=value}]"
            val outputStream = ByteArrayOutputStream()
            System.setOut(PrintStream(outputStream))
            mockMvc
                .perform(
                    post("/test/log")
                        .contentType("application/json")
                        .content("""{"key":"value"}"""),
                )

            val logOutput = outputStream.toString()

            assertThat(logOutput).contains(wantRequestLog)
            assertThat(logOutput).contains(wantResponseLog)
        }

        @Test
        @DisplayName("[성공] 본문에 요청 / 응답이 없는 경우 No Body 라는 문자열로 대체된다.")
        fun successNoBodyTest() {
            val wantRequestLog =
                "HTTP Request: [Method: POST, URI: /test/log/nobody, Headers: Content-Type: application/json;charset=UTF-8, Body: No Body]"
            val wantResponseLog = "HTTP Response: [Status: 200, Body: No Body]"
            val outputStream = ByteArrayOutputStream()
            System.setOut(PrintStream(outputStream))
            mockMvc
                .perform(
                    post("/test/log/nobody")
                        .contentType("application/json"),
                )

            val logOutput = outputStream.toString()

            assertThat(logOutput).contains(wantRequestLog)
            assertThat(logOutput).contains(wantResponseLog)
        }
    }
}

@RestController
@RequestMapping("/test")
@ActiveProfiles("test")
class TestLogController {
    @PostMapping("/log")
    fun handleRequest(
        @RequestBody body: Map<String, Any>,
    ): ResponseEntity<String> = ResponseEntity.ok("Request received: $body")
}

@RestController
@RequestMapping("/test")
@ActiveProfiles("test")
class TestLogNobodyController {
    @PostMapping("/log/nobody")
    fun handleRequest(): ResponseEntity<String> = ResponseEntity.ok().build()
}
