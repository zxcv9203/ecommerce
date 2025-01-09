package kr.hhplus.be.server.stub

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.domain.user.User

object OrderFixture {
    fun create(
        id: Long = 1L,
        user: User = UserFixture.create(),
        totalPrice: Long = 1000L,
        status: OrderStatus = OrderStatus.PENDING,
    ) = Order(
        id = id,
        user = user,
        totalPrice = totalPrice,
        status = status,
    )

    fun createOrderItem(
        id: Long = 0L,
        order: Order = create(),
        productId: Long = 1L,
        quantity: Int = 1,
    ) = OrderItem(
        id = id,
        order = order,
        productId = productId,
        count = quantity,
    )
}
