package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent

interface PaymentCompleteEventPublisher {
    fun send(event: PaymentCompletedEvent)
}
