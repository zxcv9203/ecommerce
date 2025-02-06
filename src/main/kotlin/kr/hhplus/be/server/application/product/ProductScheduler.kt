package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.domain.product.PopularProductCacheRepository
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductScheduler(
    private val productRepository: ProductRepository,
    private val popularProductCacheRepository: PopularProductCacheRepository,
) {
    @Scheduled(fixedRate = CACHE_REFRESH_INTERVAL)
    fun updatePopularProducts() {
        val popularProducts = productRepository.findPopularProducts()
        popularProductCacheRepository.save(popularProducts)
    }

    companion object {
        private const val CACHE_REFRESH_INTERVAL = 590_000L
    }
}
