package kr.hhplus.be.server.api.order.response

import kr.hhplus.be.server.domain.order.Order

data class OrderResponse(
    val orderId: Long,
)

fun Order.toResponse() =
    OrderResponse(
        orderId = id,
    )
