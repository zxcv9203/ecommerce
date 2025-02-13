package kr.hhplus.be.server.api.payment

import kr.hhplus.be.server.api.payment.request.PaymentExternalRequest
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/external/payments")
class PaymentExternalController {
    val log: Logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun savePaymentsLog(
        @RequestBody request: PaymentExternalRequest,
    ): ResponseEntity<CustomResponse<Unit>> {
        log.info("External payment API completed")

        return ResponseEntity.ok(
            CustomResponse.success(SuccessCode.EXTERNAL_PAYMENT_API_COMPLETED),
        )
    }
}
