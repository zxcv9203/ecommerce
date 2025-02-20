package kr.hhplus.be.server.infrastructure.external.payment

import kr.hhplus.be.server.application.payment.PaymentCompleteEventPublisher
import kr.hhplus.be.server.application.payment.event.PaymentCompletedEvent
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class FakePaymentCompleteEventPublisher(
    private val restClient: RestClient = RestClient.create(),
) : PaymentCompleteEventPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(event: PaymentCompletedEvent) {
        runCatching {
            restClient
                .post()
                .uri("http://localhost:8080/external/payments")
                .body(
                    mapOf(
                        "paymentId" to event.paymentId,
                        "status" to event.status,
                        "amount" to event.amount,
                    ),
                ).contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String::class.java)
        }.onSuccess { log.info("[FakeExternalPaymentClient] Success : $it") }
            .onFailure { log.error("[FakeExternalPaymentClient] Fail : ", it) }
    }
}
