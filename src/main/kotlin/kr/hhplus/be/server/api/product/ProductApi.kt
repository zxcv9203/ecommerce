package kr.hhplus.be.server.api.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.api.product.response.PopularProductsResponse
import kr.hhplus.be.server.api.product.response.ProductsResponse
import kr.hhplus.be.server.application.product.info.PopularProductsInfo
import kr.hhplus.be.server.application.product.info.ProductsInfo
import kr.hhplus.be.server.common.model.CustomResponse
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

@Tag(name = "상품 조회 API", description = "상품 목록을 조회하는 API")
interface ProductApi {
    @Operation(
        summary = "상품 목록 조회",
        description = "페이지와 정렬 방식을 지정하여 상품 목록을 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "상품 목록 조회에 성공했습니다.",
            ),
        ],
    )
    fun findAll(
        @ParameterObject
        pageable: Pageable,
    ): ResponseEntity<CustomResponse<ProductsResponse>>

    @Operation(
        summary = "인기 상품 목록 조회",
        description = "가장 많이 팔린 상품 5개를 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "가장 많이 팔린 상품 5개를 조회합니다.",
            ),
        ],
    )
    fun findPopularProducts(): ResponseEntity<CustomResponse<PopularProductsResponse>>
}
