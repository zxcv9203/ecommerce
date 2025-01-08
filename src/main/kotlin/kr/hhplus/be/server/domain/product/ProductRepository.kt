package kr.hhplus.be.server.domain.product

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ProductRepository {
    fun findAll(pageable: Pageable): Slice<Product>
}
