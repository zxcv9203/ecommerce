package kr.hhplus.be.server.api.order

import kr.hhplus.be.server.api.order.request.OrderRequest
import kr.hhplus.be.server.api.order.response.OrderResponse
import kr.hhplus.be.server.api.order.response.toResponse
import kr.hhplus.be.server.application.order.OrderUseCase
import kr.hhplus.be.server.common.constant.AuthConstants
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderUseCase: OrderUseCase,
) : OrderApi {
    @PostMapping
    override fun order(
        @RequestBody request: OrderRequest,
        @RequestAttribute(AuthConstants.AUTH_ID) authenticationId: Long,
    ): ResponseEntity<CustomResponse<OrderResponse>> =
        request
            .toCommand(authenticationId)
            .let { orderUseCase.order(it) }
            .toResponse()
            .let { CustomResponse.success(SuccessCode.ORDER_CREATED, it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
}
