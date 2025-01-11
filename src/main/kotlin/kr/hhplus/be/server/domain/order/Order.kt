package kr.hhplus.be.server.domain.order

import jakarta.persistence.*
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.BaseEntity
import kr.hhplus.be.server.domain.user.User
import org.hibernate.annotations.Comment

@Entity
@Table(name = "orders")
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("주문자")
    val user: User,
    @Column(name = "total_price", nullable = false)
    @Comment("총 가격")
    val totalPrice: Long,
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("주문 상태")
    var status: OrderStatus = OrderStatus.PENDING,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun ensureNotPaid() {
        if (status != OrderStatus.PENDING) {
            throw BusinessException(ErrorCode.ORDER_ALREADY_PROCESSED)
        }
    }

    fun confirm() {
        ensureNotPaid()
        status = OrderStatus.CONFIRMED
    }
}
