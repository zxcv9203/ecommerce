package kr.hhplus.be.server.domain.order

import kr.hhplus.be.server.application.product.info.OrderItemInfo
import kr.hhplus.be.server.application.product.info.getTotalPrice
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
    ): Order {
        val totalPrice = products.getTotalPrice()
        coupon?.getDiscountedPrice(totalPrice)

        val order = Order(user, totalPrice)
        orderRepository.save(order)
        products
            .map { it.toOrderItem(order) }
            .let { orderItemRepository.saveAll(it) }

        return order
    }
}
