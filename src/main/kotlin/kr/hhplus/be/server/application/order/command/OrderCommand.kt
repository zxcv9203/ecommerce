package kr.hhplus.be.server.application.order.command

data class OrderCommand(
    val userId: Long,
    val items: List<OrderItemCommand>,
    val couponId: Long?,
)

data class OrderItemCommand(
    val productId: Long,
    val quantity: Int,
)
