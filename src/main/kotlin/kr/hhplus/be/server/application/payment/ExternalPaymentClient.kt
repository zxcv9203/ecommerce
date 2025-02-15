package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.domain.payment.PaymentStatus

interface ExternalPaymentClient {
    fun sendPaymentResult(
        paymentId: Long,
        status: PaymentStatus,
        amount: Long,
    )
}
