package kr.hhplus.be.server.infrastructure.persistence.payment

import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import org.springframework.stereotype.Repository

@Repository
class JpaPaymentRepository(
    private val dataJpaPaymentRepository: DataJpaPaymentRepository,
) : PaymentRepository {
    override fun save(payment: Payment): Payment = dataJpaPaymentRepository.save(payment)
}
