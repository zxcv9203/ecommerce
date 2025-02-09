package kr.hhplus.be.server.template

import kr.hhplus.be.server.helper.DatabaseCleanUp
import kr.hhplus.be.server.helper.RedisCleanup
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var databaseCleanUp: DatabaseCleanUp

    @Autowired
    protected lateinit var redisCleanup: RedisCleanup

    protected val objectMapper = ObjectMapper()

    @BeforeEach
    fun cleanUp() {
        databaseCleanUp.execute()
        redisCleanup.execute()
    }
}
