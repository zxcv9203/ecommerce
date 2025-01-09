package kr.hhplus.be.server.infrastructure.persistence.product

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.product.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface JpaProductRepository : JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
            SELECT p
            FROM Product p
            WHERE p.id IN :ids
        """,
    )
    fun findAllByIdsWithLock(ids: List<Long>): List<Product>
}
