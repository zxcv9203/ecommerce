package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.application.payment.event.PaymentCompleteEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentCompleteEventHandler(
    private val externalPaymentClient: ExternalPaymentClient,
) {
    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaymentCompleteEvent) {
        externalPaymentClient.sendPaymentResult(event.paymentId, event.status, event.amount)
    }
}
