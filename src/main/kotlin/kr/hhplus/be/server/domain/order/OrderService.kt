package kr.hhplus.be.server.domain.order

import kr.hhplus.be.server.application.product.info.OrderItemInfo
import kr.hhplus.be.server.application.product.info.getTotalPrice
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
) {
    @Transactional
    fun create(
        user: User,
        products: List<OrderItemInfo>,
        coupon: Coupon?,
        paymentPrice: Long,
    ): Order {
        val totalPrice = products.getTotalPrice()

        val order = Order.create(user, totalPrice, paymentPrice)
        orderRepository.save(order)

        products
            .map { it.toOrderItem(order) }
            .let { orderItemRepository.saveAll(it) }

        return order
    }

    fun getByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): Order =
        orderRepository.findByIdAndUserIdWithLock(id, userId)
            ?: throw BusinessException(ErrorCode.ORDER_NOT_FOUND)

    fun findOrderItems(orderId: Long): List<OrderItem> = orderItemRepository.findByOrderId(orderId)

    @Transactional
    fun confirm(order: Order) {
        order.confirm()
        orderRepository.save(order)
    }
}
