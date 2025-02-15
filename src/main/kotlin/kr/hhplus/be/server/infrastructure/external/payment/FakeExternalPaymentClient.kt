package kr.hhplus.be.server.infrastructure.external.payment

import kr.hhplus.be.server.application.payment.ExternalPaymentClient
import kr.hhplus.be.server.domain.payment.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class FakeExternalPaymentClient(
    private val restClient: RestClient = RestClient.create(),
) : ExternalPaymentClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun sendPaymentResult(
        paymentId: Long,
        status: PaymentStatus,
        amount: Long,
    ) {
        runCatching {
            restClient
                .post()
                .uri("http://localhost:8080/external/payments")
                .body(
                    mapOf(
                        "paymentId" to paymentId,
                        "status" to status,
                        "amount" to amount,
                    ),
                ).contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String::class.java)
        }.onSuccess { log.info("[FakeExternalPaymentClient] Success : $it") }
            .onFailure { log.error("[FakeExternalPaymentClient] Fail : ", it) }
    }
}
