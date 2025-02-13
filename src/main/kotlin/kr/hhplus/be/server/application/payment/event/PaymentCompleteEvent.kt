package kr.hhplus.be.server.application.payment.event

import kr.hhplus.be.server.domain.payment.PaymentStatus

data class PaymentCompleteEvent(
    val paymentId: Long,
    val status: PaymentStatus,
    val amount: Long,
)
