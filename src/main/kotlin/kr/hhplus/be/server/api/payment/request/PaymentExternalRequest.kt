package kr.hhplus.be.server.api.payment.request

data class PaymentExternalRequest(
    val paymentId: Long,
    val status: String,
    val amount: Long,
)
