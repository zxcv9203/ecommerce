package kr.hhplus.be.server.domain.payment

interface ExternalPaymentClient {
    fun sendPaymentResult(
        paymentId: Long,
        status: PaymentStatus,
    )
}
