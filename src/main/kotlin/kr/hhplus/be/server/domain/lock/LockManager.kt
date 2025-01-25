package kr.hhplus.be.server.domain.lock

interface LockManager {
    fun <T> withLock(
        key: String,
        block: () -> T,
    ): T
}
