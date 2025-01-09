package kr.hhplus.be.server.api.product

import kr.hhplus.be.server.api.product.response.PopularProductResponse
import kr.hhplus.be.server.api.product.response.PopularProductsResponse
import kr.hhplus.be.server.api.product.response.ProductsResponse
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
            .let { CustomResponse.success(SuccessCode.PRODUCT_QUERY, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }

    @GetMapping("/popular-products")
    override fun findPopularProducts(): ResponseEntity<CustomResponse<PopularProductsResponse>> {
        val products =
            listOf(
                PopularProductResponse(1, "상품 A", 12000, 150),
                PopularProductResponse(2, "상품 B", 8000, 120),
                PopularProductResponse(3, "상품 C", 5000, 100),
                PopularProductResponse(4, "상품 D", 15000, 80),
                PopularProductResponse(5, "상품 E", 7000, 60),
            )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomResponse.success(SuccessCode.POPULAR_PRODUCT_QUERY, PopularProductsResponse(products)))
    }
}
