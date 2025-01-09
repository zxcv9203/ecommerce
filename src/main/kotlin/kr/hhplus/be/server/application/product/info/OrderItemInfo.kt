package kr.hhplus.be.server.application.product.info

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.product.Product

data class OrderItemInfo(
    val id: Long,
    val price: Long,
    val quantity: Int,
) {
    fun toOrderItem(order: Order) = OrderItem(order, id, quantity)
}

fun List<OrderItemInfo>.getTotalPrice() = sumOf { it.price * it.quantity }

fun Product.toOrderProductInfo(quantity: Int) =
    OrderItemInfo(
        id = id,
        price = price,
        quantity = quantity,
    )
