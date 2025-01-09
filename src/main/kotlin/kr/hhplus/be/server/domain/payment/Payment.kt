package kr.hhplus.be.server.domain.payment

import jakarta.persistence.*
import kr.hhplus.be.server.common.model.BaseEntity
import kr.hhplus.be.server.domain.order.Order
import org.hibernate.annotations.Comment

@Entity
@Table(name = "payments")
class Payment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Comment("주문")
    val order: Order,
    @Column(name = "amount", nullable = false)
    @Comment("결제 금액")
    val amount: Long,
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("결제 수단")
    val type: PaymentType = PaymentType.BALANCE,
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("결제 상태")
    val status: PaymentStatus = PaymentStatus.SUCCESS,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
