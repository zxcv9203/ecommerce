package kr.hhplus.be.server.api.product

import kr.hhplus.be.server.api.product.response.PopularProductsResponse
import kr.hhplus.be.server.api.product.response.ProductsResponse
import kr.hhplus.be.server.api.product.response.toResponse
import kr.hhplus.be.server.application.product.ProductUseCase
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProductController(
    private val productUseCase: ProductUseCase,
) : ProductApi {
    @GetMapping("/products")
    override fun findAll(pageable: Pageable): ResponseEntity<CustomResponse<ProductsResponse>> =
        productUseCase
            .findAll(pageable)
            .toResponse()
            .let { CustomResponse.success(SuccessCode.PRODUCT_QUERY, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }

    @GetMapping("/popular-products")
    override fun findPopularProducts(): ResponseEntity<CustomResponse<PopularProductsResponse>> =
        productUseCase
            .findPopularProducts()
            .toResponse()
            .let { CustomResponse.success(SuccessCode.POPULAR_PRODUCT_QUERY, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
