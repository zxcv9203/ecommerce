package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.application.order.command.toCommand
import kr.hhplus.be.server.application.payment.command.PaymentCommand
import kr.hhplus.be.server.application.payment.event.PaymentCompleteEvent
import kr.hhplus.be.server.application.user.command.toUseBalanceCommand
import kr.hhplus.be.server.domain.coupon.CouponService
import kr.hhplus.be.server.domain.order.OrderService
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.product.ProductService
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentUseCase(
    private val userService: UserService,
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val productService: ProductService,
    private val couponService: CouponService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun pay(command: PaymentCommand) {
        val user = userService.getById(command.userId)

        val order =
            orderService
                .getByIdAndUserIdWithLock(command.orderId, user.id)
                .also { it.ensureNotPaid() }

        val orderItemsCommand =
            orderService
                .findOrderItems(order.id)
                .map { it.toCommand() }

        val coupon = couponService.findByOrderId(order.id)
        coupon?.let { couponService.use(it) }
        userService.useBalance(user.toUseBalanceCommand(order.discountPrice))
        productService.reduceStock(orderItemsCommand)
        orderService.confirm(order)
        val payment = paymentService.pay(order)
        eventPublisher.publishEvent(PaymentCompleteEvent(payment.id, payment.status, payment.amount))
    }
}
