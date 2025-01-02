package kr.hhplus.be.server.api.order.request

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>,
    val couponId: Long?,
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)
