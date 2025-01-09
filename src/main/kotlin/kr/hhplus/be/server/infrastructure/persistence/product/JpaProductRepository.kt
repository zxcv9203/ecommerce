package kr.hhplus.be.server.infrastructure.persistence.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaProductRepository :
    ProductRepository,
    JpaRepository<Product, Long> {
    @Query(
        """
            SELECT p
            FROM Product p
            WHERE p.id IN :ids
        """,
    )
    override fun findAllByIds(ids: List<Long>): List<Product>
}
