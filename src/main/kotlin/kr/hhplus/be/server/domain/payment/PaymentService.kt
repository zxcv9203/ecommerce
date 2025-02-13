package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.domain.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun pay(order: Order) = paymentRepository.save(Payment(order, order.discountPrice))
}
