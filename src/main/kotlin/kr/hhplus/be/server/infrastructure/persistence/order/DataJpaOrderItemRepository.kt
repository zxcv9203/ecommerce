package kr.hhplus.be.server.infrastructure.persistence.order

import kr.hhplus.be.server.domain.order.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface DataJpaOrderItemRepository : JpaRepository<OrderItem, Long> {
    fun findByOrderId(orderId: Long): List<OrderItem>
}
