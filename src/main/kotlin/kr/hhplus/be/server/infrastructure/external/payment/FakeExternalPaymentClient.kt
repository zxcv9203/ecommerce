package kr.hhplus.be.server.infrastructure.external.payment

import kr.hhplus.be.server.domain.payment.ExternalPaymentClient
import kr.hhplus.be.server.domain.payment.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FakeExternalPaymentClient : ExternalPaymentClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun sendPaymentResult(
        paymentId: Long,
        status: PaymentStatus,
    ) {
        log.info("send payment result: paymentId=$paymentId, status=$status")
    }
}
