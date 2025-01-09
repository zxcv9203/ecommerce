package kr.hhplus.be.server.infrastructure.persistence.payment

import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPaymentRepository :
    PaymentRepository,
    JpaRepository<Payment, Long>
