package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
import kr.hhplus.be.server.common.model.BaseEntity
import org.hibernate.annotations.Comment

@Entity
@Table(name = "balance_histories")
class BalanceHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    val user: User,
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("잔액 변동 유형")
    val type: BalanceHistoryType,
    @Column(name = "amount", nullable = false)
    @Comment("잔액 변동 금액")
    val amount: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    companion object {
        fun createByCharge(
            user: User,
            amount: Long,
        ) = BalanceHistory(
            user = user,
            type = BalanceHistoryType.CHARGE,
            amount = amount,
        )
    }
}
