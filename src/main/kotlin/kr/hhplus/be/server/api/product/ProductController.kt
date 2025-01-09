package kr.hhplus.be.server.api.product

import kr.hhplus.be.server.api.product.response.PopularProductResponse
import kr.hhplus.be.server.api.product.response.PopularProductsResponse
import kr.hhplus.be.server.api.product.response.ProductResponse
import kr.hhplus.be.server.api.product.response.ProductsResponse
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProductController : ProductApi {
    @GetMapping("/products")
    override fun findAll(pageable: Pageable): ResponseEntity<CustomResponse<ProductsResponse>> {
        val products =
            listOf(
                ProductResponse(1, "상품1", 10000, 10),
                ProductResponse(2, "상품2", 1000, 1),
                ProductResponse(3, "상품3", 25000, 20),
                ProductResponse(4, "상품4", 13000, 5),
                ProductResponse(5, "상품5", 23000, 30),
                ProductResponse(6, "상품6", 33000, 15),
                ProductResponse(7, "상품7", 41000, 25),
                ProductResponse(8, "상품8", 80000, 3),
                ProductResponse(9, "상품9", 90000, 40),
                ProductResponse(10, "상품10", 100000, 50),
                ProductResponse(11, "상품11", 110000, 0),
            )
        val sortedProducts =
            if (pageable.sort.isSorted) {
                products.sortedWith { a, b ->
                    pageable.sort
                        .map { order ->
                            val property = order.property
                            val direction = order.direction
                            val compareResult =
                                when (property) {
                                    "id" -> a.id.compareTo(b.id)
                                    "name" -> a.name.compareTo(b.name)
                                    "price" -> a.price.compareTo(b.price)
                                    "stock" -> a.stock.compareTo(b.stock)
                                    else -> 0
                                }
                            if (direction.isAscending) compareResult else -compareResult
                        }.firstOrNull { it != 0 } ?: 0
                }
            } else {
                products
            }

        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(sortedProducts.size)
        val paginatedProducts = if (start < sortedProducts.size) sortedProducts.subList(start, end) else emptyList()

        val page = PageImpl(paginatedProducts, pageable, sortedProducts.size.toLong())

        val response =
            ProductsResponse(
                hasNext = page.hasNext(),
                products = page.content,
            )
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomResponse.success(SuccessCode.PRODUCT_QUERY, response))
    }

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
