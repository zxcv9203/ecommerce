package kr.hhplus.be.server.api.payment

import kr.hhplus.be.server.api.payment.request.PaymentRequest
import kr.hhplus.be.server.application.payment.PaymentUseCase
import kr.hhplus.be.server.common.constant.AuthConstants
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentUseCase: PaymentUseCase,
) : PaymentApi {
    @PostMapping
    override fun pay(
        @RequestBody request: PaymentRequest,
        @RequestAttribute(AuthConstants.AUTH_ID) authenticationId: Long,
    ): ResponseEntity<CustomResponse<Unit>> =
        request
            .toCommand(authenticationId)
            .let { paymentUseCase.pay(it) }
            .let { CustomResponse.success(SuccessCode.PAYMENT_COMPLETED) }
            .let { ResponseEntity.ok(it) }
}
