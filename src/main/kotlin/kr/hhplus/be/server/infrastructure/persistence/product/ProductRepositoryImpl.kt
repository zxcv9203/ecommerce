package kr.hhplus.be.server.infrastructure.persistence.product

import kr.hhplus.be.server.api.product.response.PopularProductResponse
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaProductRepository: JpaProductRepository,
) : ProductRepository {
    override fun findAll(pageable: Pageable): Slice<Product> = jpaProductRepository.findAll(pageable)

    override fun findAllByIds(ids: List<Long>): List<Product> = jpaProductRepository.findAllById(ids)

    override fun findAllByIdsWithLock(ids: List<Long>): List<Product> = jpaProductRepository.findAllByIdsWithLock(ids)

    override fun saveAll(products: List<Product>): List<Product> = jpaProductRepository.saveAll(products)

    override fun findPopularProducts(): List<PopularProductResponse> = jpaProductRepository.findPopularProducts()
}
