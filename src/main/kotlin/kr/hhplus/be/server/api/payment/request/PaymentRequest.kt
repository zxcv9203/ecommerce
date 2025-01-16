package kr.hhplus.be.server.api.payment.request

import kr.hhplus.be.server.application.payment.command.PaymentCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException

data class PaymentRequest(
    val userId: Long,
    val orderId: Long,
) {
    fun toCommand(authenticationId: Long): PaymentCommand {
        if (userId != authenticationId) throw BusinessException(ErrorCode.FORBIDDEN)
        return PaymentCommand(
            userId = userId,
            orderId = orderId,
        )
    }
}
