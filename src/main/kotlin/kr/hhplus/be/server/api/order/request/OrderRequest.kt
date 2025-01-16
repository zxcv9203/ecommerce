package kr.hhplus.be.server.api.order.request

import kr.hhplus.be.server.application.order.command.OrderCommand
import kr.hhplus.be.server.application.order.command.OrderItemCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>,
    val couponId: Long?,
) {
    fun toCommand(authenticationId: Long): OrderCommand {
        if (userId != authenticationId) throw BusinessException(ErrorCode.FORBIDDEN)

        return OrderCommand(
            userId = userId,
            items = items.map { it.toCommand() },
            couponId = couponId,
        )
    }
}

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int,
) {
    fun toCommand() =
        OrderItemCommand(
            productId = productId,
            quantity = quantity,
        )
}
