package kr.hhplus.be.server.api.payment.request

data class PaymentRequest(
    val userId: Long,
    val orderId: Long,
)
