package kr.hhplus.be.server.infrastructure.persistence.order

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class JpaOrderRepository(
    private val dataJpaOrderRepository: DataJpaOrderRepository,
) : OrderRepository {
    override fun save(order: Order): Order = dataJpaOrderRepository.save(order)

    override fun findByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): Order? = dataJpaOrderRepository.findByIdAndUserIdWithLock(id, userId)
}
