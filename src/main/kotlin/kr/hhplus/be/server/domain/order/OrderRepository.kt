package kr.hhplus.be.server.domain.order

interface OrderRepository {
    fun save(order: Order): Order

    fun findByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): Order?
}
