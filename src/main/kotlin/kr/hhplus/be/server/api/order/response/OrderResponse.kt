package kr.hhplus.be.server.api.order.response

import kr.hhplus.be.server.application.order.info.OrderInfo

data class OrderResponse(
    val orderId: Long,
)

fun OrderInfo.toResponse() =
    OrderResponse(
        orderId = orderId,
    )
