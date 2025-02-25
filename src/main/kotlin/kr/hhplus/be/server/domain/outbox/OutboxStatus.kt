package kr.hhplus.be.server.domain.outbox

enum class OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED,
}
