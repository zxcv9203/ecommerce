package kr.hhplus.be.server.infrastructure.lock

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.lock.LockManager
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedissonLockManager(
    private val redissonClient: RedissonClient,
) : LockManager {
    override fun <T> withLock(
        key: String,
        block: () -> T,
    ): T {
        val lock = redissonClient.getLock(key)
        return if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
            try {
                block()
            } finally {
                lock.unlock()
            }
        } else {
            throw BusinessException(ErrorCode.LOCK_TIMEOUT)
        }
    }
}
