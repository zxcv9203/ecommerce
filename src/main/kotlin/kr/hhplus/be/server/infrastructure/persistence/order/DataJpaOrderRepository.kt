package kr.hhplus.be.server.infrastructure.persistence.order

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.order.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface DataJpaOrderRepository : JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
            SELECT o
            FROM Order o
            WHERE o.id = :id AND o.user.id = :userId
        """,
    )
    fun findByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): Order?
}
