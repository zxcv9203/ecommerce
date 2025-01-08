package kr.hhplus.be.server.infrastructure.persistence.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductRepository :
    ProductRepository,
    JpaRepository<Product, Long>
