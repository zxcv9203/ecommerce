package kr.hhplus.be.server.stub

import kr.hhplus.be.server.domain.order.Order
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
}
