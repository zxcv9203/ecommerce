package kr.hhplus.be.server.application.order.info

import kr.hhplus.be.server.domain.order.Order

data class OrderInfo(
    val orderId: Long,
)

fun Order.toResponse() =
    OrderInfo(
        orderId = id,
    )
