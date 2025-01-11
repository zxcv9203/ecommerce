package kr.hhplus.be.server.api.payment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.api.payment.request.PaymentRequest
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.ResponseEntity

@Tag(name = "결제 API", description = "주문한 상품을 결제하는 API")
interface PaymentApi {
    @Operation(
        summary = "결제하기",
        description = "주문한 상품을 결제합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "결제가 성공적으로 완료되었습니다.",
            ),
            ApiResponse(
                responseCode = "404",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다:\n" +
                        "- 존재하지 않는 사용자입니다.\n" +
                        "- 존재하지 않는 주문입니다.",
            ),
            ApiResponse(
                responseCode = "400",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다:\n" +
                        "- 이미 처리된 주문입니다.\n" +
                        "- 상품 재고가 부족합니다.\n" +
                        "- 잔액이 부족합니다.",
            ),
        ],
    )
    fun pay(
        @RequestBody(description = "결제 요청 정보", required = true)
        request: PaymentRequest,
    ): ResponseEntity<CustomResponse<Unit>>
}
