package kr.hhplus.be.server.domain.order

interface OrderItemRepository {
    fun saveAll(orderItems: List<OrderItem>): List<OrderItem>
}
