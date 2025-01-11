package kr.hhplus.be.server.application.order.command

import kr.hhplus.be.server.domain.order.OrderItem

data class OrderCommand(
    val userId: Long,
    val items: List<OrderItemCommand>,
    val couponId: Long?,
)

data class OrderItemCommand(
    val productId: Long,
    val quantity: Int,
)

fun OrderItem.toCommand() = OrderItemCommand(productId, count)
