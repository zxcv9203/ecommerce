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
    fun pay(
        order: Order,
        amount: Long,
    ) {
        val payment = paymentRepository.save(Payment(order, amount))

        externalPaymentClient.sendPaymentResult(payment.id, PaymentStatus.SUCCESS)
    }

}
