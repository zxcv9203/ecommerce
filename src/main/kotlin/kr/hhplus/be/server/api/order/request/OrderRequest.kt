package kr.hhplus.be.server.api.order.request

import kr.hhplus.be.server.application.order.command.OrderCommand
import kr.hhplus.be.server.application.order.command.OrderItemCommand

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>,
    val couponId: Long?,
) {
    fun toCommand() =
        OrderCommand(
            userId = userId,
            items = items.map { it.toCommand() },
            couponId = couponId,
        )
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
