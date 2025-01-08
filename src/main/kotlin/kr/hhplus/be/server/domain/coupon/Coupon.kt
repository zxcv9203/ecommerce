package kr.hhplus.be.server.domain.coupon

import jakarta.persistence.*
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.BaseEntity
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.user.User
import org.hibernate.annotations.Comment

@Entity
@Table(name = "coupons")
class Coupon(
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @Comment("쿠폰 정책")
    val policy: CouponPolicy,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("쿠폰 보유 사용자")
    val user: User,
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태")
    val status: CouponStatus,
    @ManyToOne
    @JoinColumn(name = "order_id")
    @Comment("쿠폰 사용 주문")
    val order: Order? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()

fun Coupon?.checkAlreadyIssue() {
    if (this != null) {
        throw BusinessException(ErrorCode.COUPON_ALREADY_ISSUED)
    }
}
