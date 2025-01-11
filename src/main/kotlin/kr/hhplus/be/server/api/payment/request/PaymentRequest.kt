package kr.hhplus.be.server.api.payment.request

import kr.hhplus.be.server.application.payment.command.PaymentCommand

data class PaymentRequest(
    val userId: Long,
    val orderId: Long,
) {
    fun toCommand() =
        PaymentCommand(
            userId = userId,
            orderId = orderId,
        )
}
