package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.api.product.response.ProductResponse
import kr.hhplus.be.server.api.product.response.toResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findAll(pageable: Pageable): Slice<ProductResponse> =
        productRepository
            .findAll(pageable)
            .map { it.toResponse() }
}
