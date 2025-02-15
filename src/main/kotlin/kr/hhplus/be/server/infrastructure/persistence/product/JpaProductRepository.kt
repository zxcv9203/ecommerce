package kr.hhplus.be.server.infrastructure.persistence.product

import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class JpaProductRepository(
    private val dataJpaProductRepository: DataJpaProductRepository,
) : ProductRepository {
    override fun findAll(pageable: Pageable): Slice<Product> = dataJpaProductRepository.findAll(pageable)

    override fun findAllByIds(ids: List<Long>): List<Product> = dataJpaProductRepository.findAllById(ids)

    override fun findAllByIdsWithLock(ids: List<Long>): List<Product> = dataJpaProductRepository.findAllByIdsWithLock(ids)

    override fun saveAll(products: List<Product>): List<Product> = dataJpaProductRepository.saveAll(products)

    override fun findPopularProducts(datetime: LocalDateTime): List<PopularProductInfo> = dataJpaProductRepository.findPopularProducts(datetime)
}
