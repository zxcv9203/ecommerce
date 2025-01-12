package kr.hhplus.be.server.infrastructure.persistence.payment

import kr.hhplus.be.server.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface DataJpaPaymentRepository : JpaRepository<Payment, Long>
