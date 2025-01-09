package kr.hhplus.be.server.api.payment

import kr.hhplus.be.server.api.payment.request.PaymentRequest
import kr.hhplus.be.server.application.payment.PaymentUseCase
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentUseCase: PaymentUseCase,
) : PaymentApi {
    @PostMapping
    override fun pay(
        @RequestBody request: PaymentRequest,
    ): ResponseEntity<CustomResponse<Unit>> =
        request
            .toCommand()
            .let { paymentUseCase.pay(it) }
            .let { CustomResponse.success(SuccessCode.PAYMENT_COMPLETED) }
            .let { ResponseEntity.ok(it) }
}
