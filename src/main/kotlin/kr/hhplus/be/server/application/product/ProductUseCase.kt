package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.application.product.info.PopularProductsInfo
import kr.hhplus.be.server.application.product.info.ProductsInfo
import kr.hhplus.be.server.domain.product.ProductService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProductUseCase(
    private val productService: ProductService,
) {
    fun findAll(pageable: Pageable) =
        productService
            .findAll(pageable)
            .let { ProductsInfo(it.content, it.hasNext()) }

    fun findPopularProducts() =
        productService
            .findPopularProducts()
            .let { PopularProductsInfo(it) }
}
