package kr.hhplus.be.server.infrastructure.persistence.order

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface JpaOrderRepository :
    OrderRepository,
    JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
            SELECT o
            FROM Order o
            WHERE o.id = :id AND o.user.id = :userId
        """,
    )
    override fun findByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): Order?
}
