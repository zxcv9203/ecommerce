package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.domain.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val externalPaymentClient: ExternalPaymentClient,
) {
    @Transactional
    fun pay(order: Order) {
        val payment = paymentRepository.save(Payment(order, order.discountPrice))

        externalPaymentClient.sendPaymentResult(payment.id, PaymentStatus.SUCCESS)
    }
}
