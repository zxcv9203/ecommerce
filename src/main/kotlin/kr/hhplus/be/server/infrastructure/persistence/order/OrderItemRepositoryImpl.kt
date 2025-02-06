package kr.hhplus.be.server.infrastructure.persistence.order

import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import org.springframework.stereotype.Repository

@Repository
class OrderItemRepositoryImpl(
    private val dataJpaOrderItemRepository: DataJpaOrderItemRepository,
) : OrderItemRepository {
    override fun saveAll(orderItems: List<OrderItem>): List<OrderItem> = dataJpaOrderItemRepository.saveAll(orderItems)

    override fun findByOrderId(orderId: Long): List<OrderItem> = dataJpaOrderItemRepository.findByOrderId(orderId)
}
