package kr.hhplus.be.server.api.payment

import kr.hhplus.be.server.api.payment.request.PaymentRequest
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController : PaymentApi {
    @PostMapping
    override fun pay(
        @RequestBody request: PaymentRequest,
    ): ResponseEntity<CustomResponse<Unit>> {
        if (request.userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val orderStatus =
            mapOf(
                1L to "PENDING",
                2L to "COMPLETED",
                3L to "OUT_OF_STOCK",
                4L to "INSUFFICIENT_BALANCE",
            )

        val status = orderStatus[request.orderId] ?: throw BusinessException(ErrorCode.ORDER_NOT_FOUND)

        when (status) {
            "PENDING" -> { // Do nothing
            }

            "COMPLETED" -> throw BusinessException(ErrorCode.ORDER_ALREADY_PROCESSED)
            "OUT_OF_STOCK" -> throw BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK)
            "INSUFFICIENT_BALANCE" -> throw BusinessException(ErrorCode.INSUFFICIENT_BALANCE)
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomResponse.success(SuccessCode.PAYMENT_COMPLETED))
    }
}
