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
    var status: CouponStatus,
    @ManyToOne
    @JoinColumn(name = "order_id")
    @Comment("쿠폰 사용 주문")
    var order: Order? = null,
    @Version
    @Column(name = "version", nullable = false)
    @Comment("낙관적락을 위한 버전")
    val version: Long = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun ensureOwner(user: User) {
        if (this.user != user) {
            throw BusinessException(ErrorCode.COUPON_OWNER_MISMATCH)
        }
    }

    fun ensureUsableStatus() {
        if (status != CouponStatus.ACTIVE) {
            throw BusinessException(ErrorCode.COUPON_ALREADY_USED)
        }
    }

    fun getDiscountedPrice(price: Long): Long {
        val calculatedPrice =
            when (policy.discountType) {
                CouponDiscountType.AMOUNT -> price - policy.discountAmount
                CouponDiscountType.PERCENT -> (price * (100 - policy.discountAmount) / 100)
            }
        if (calculatedPrice <= 0) {
            throw BusinessException(ErrorCode.ORDER_AMOUNT_INVALID)
        }
        return calculatedPrice
    }

    fun reserve(order: Order) {
        if (status != CouponStatus.ACTIVE) {
            throw BusinessException(ErrorCode.COUPON_ALREADY_USED)
        }

        status = CouponStatus.RESERVED
        this.order = order
    }

    fun use() {
        if (status != CouponStatus.RESERVED) {
            throw BusinessException(ErrorCode.COUPON_NOT_ORDER_RESERVED)
        }

        status = CouponStatus.USED
    }
}

fun Coupon?.checkAlreadyIssue() {
    if (this != null) {
        throw BusinessException(ErrorCode.COUPON_ALREADY_ISSUED)
    }
}
