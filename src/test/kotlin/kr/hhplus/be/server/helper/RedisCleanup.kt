package kr.hhplus.be.server.helper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Component
class RedisCleanup {
    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    fun execute() {
        redisTemplate
            .connectionFactory
            ?.connection
            ?.serverCommands()
            ?.flushAll()
    }
}
