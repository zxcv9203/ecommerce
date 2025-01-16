package kr.hhplus.be.server.api.order

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.api.order.request.OrderRequest
import kr.hhplus.be.server.api.order.response.OrderResponse
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.ResponseEntity

@Tag(name = "주문 API", description = "선택한 상품을 주문하는 API")
interface OrderApi {
    @Operation(
        summary = "주문하기",
        description = "선택한 상품을 주문합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "주문 성공"),
            ApiResponse(
                responseCode = "404",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다:\n" +
                        "- 존재하지 않는 사용자입니다.\n" +
                        "- 존재하지 않는 상품입니다.\n" +
                        "- 존재하지 않는 쿠폰입니다.",
            ),
            ApiResponse(
                responseCode = "400",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다:\n" +
                        "- 쿠폰이 만료되었습니다.\n" +
                        "- 이미 사용된 쿠폰입니다.\n" +
                        "- 상품 재고가 부족합니다.\n" +
                        "- 주문 금액은 0원 이하일 수 없습니다.",
            ),
        ],
    )
    fun order(
        @RequestBody(description = "주문 요청 정보", required = true)
        request: OrderRequest,
        authenticationId: Long,
    ): ResponseEntity<CustomResponse<OrderResponse>>
}
