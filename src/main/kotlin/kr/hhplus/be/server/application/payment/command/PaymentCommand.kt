package kr.hhplus.be.server.application.payment.command

data class PaymentCommand(
    val userId: Long,
    val orderId: Long,
)
