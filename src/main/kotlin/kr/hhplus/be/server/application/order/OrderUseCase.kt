package kr.hhplus.be.server.application.order

import kr.hhplus.be.server.api.order.response.OrderResponse
import kr.hhplus.be.server.api.order.response.toResponse
import kr.hhplus.be.server.application.order.command.OrderCommand
import kr.hhplus.be.server.application.product.info.getTotalPrice
import kr.hhplus.be.server.domain.coupon.CouponService
import kr.hhplus.be.server.domain.discount.DiscountService
import kr.hhplus.be.server.domain.order.OrderService
import kr.hhplus.be.server.domain.product.ProductService
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderUseCase(
    private val userService: UserService,
    private val productService: ProductService,
    private val couponService: CouponService,
    private val orderService: OrderService,
    private val discountService: DiscountService,
) {
    @Transactional
    fun order(command: OrderCommand): OrderResponse {
        val user = userService.getById(command.userId)
        val products = productService.findOrderableProductByIds(command.items)

        val coupon =
            command.couponId
                ?.let { couponService.getOrderableCoupon(it, user) }

        val totalPrice = products.getTotalPrice()
        val paymentPrice = discountService.calculateDiscountedPrice(coupon, totalPrice)
        val order = orderService.create(user, products, coupon, paymentPrice)

        coupon?.let { couponService.reserve(it, order) }

        return order.toResponse()
    }
}
