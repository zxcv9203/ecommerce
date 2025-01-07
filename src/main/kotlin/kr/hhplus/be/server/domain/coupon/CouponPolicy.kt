package kr.hhplus.be.server.domain.coupon

import jakarta.persistence.*
import kr.hhplus.be.server.common.model.BaseEntity
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
@Table(name = "coupon_policies")
class CouponPolicy(
    @Column(name = "name", nullable = false)
    @Comment("쿠폰 정책 이름")
    val name: String,
    @Column(name = "description", nullable = false, length = 512)
    @Comment("쿠폰 정책 설명")
    val description: String,
    @Column(name = "total_count", nullable = false)
    @Comment("총 쿠폰 수량")
    val totalCount: Int,
    @Column(name = "current_count", nullable = false)
    @Comment("현재 쿠폰 수량")
    val currentCount: Int,
    @Column(name = "start_time", nullable = false)
    @Comment("시작 시간")
    val startTime: LocalDateTime,
    @Column(name = "end_time", nullable = false)
    @Comment("종료 시간")
    val endTime: LocalDateTime,
    @Column(name = "discount_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("할인 유형")
    val discountType: CouponDiscountType,
    @Column(name = "discount_amount", nullable = false)
    @Comment("할인 금액")
    val discountAmount: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
