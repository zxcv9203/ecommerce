package kr.hhplus.be.server.infrastructure.persistence.product

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.api.product.response.PopularProductResponse
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

    @Query(
        """
    SELECT new kr.hhplus.be.server.api.product.response.PopularProductResponse(
        p.id,
        p.name, 
        p.price,
        SUM(oi.count)
    )
    FROM Product p
    JOIN OrderItem oi ON p.id = oi.productId
    JOIN Order o ON oi.order.id = o.id
    WHERE o.status = 'CONFIRMED'
    GROUP BY p.id, p.name, p.price
    ORDER BY SUM(oi.count) DESC
    LIMIT 5
    """,
    )
    fun findPopularProducts(): List<PopularProductResponse>
}
