package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.api.product.response.PopularProductsResponse
import kr.hhplus.be.server.api.product.response.ProductsResponse
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
            .let { ProductsResponse(it.content, it.hasNext()) }

    fun findPopularProducts() =
        productService
            .findPopularProducts()
            .let { PopularProductsResponse(it) }
}
