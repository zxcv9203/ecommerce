package kr.hhplus.be.server.domain.product

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ProductRepository {
    fun findAll(pageable: Pageable): Slice<Product>

    fun findAllByIds(ids: List<Long>): List<Product>

    fun findAllByIdsWithLock(ids: List<Long>): List<Product>

    fun saveAll(products: List<Product>): List<Product>
}
