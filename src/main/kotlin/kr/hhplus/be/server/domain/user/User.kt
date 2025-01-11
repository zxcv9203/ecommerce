package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.BaseEntity
import org.hibernate.annotations.Comment

@Entity
@Table(name = "users")
class User(
    @Column(name = "name", nullable = false, length = 50)
    @Comment("사용자 이름")
    val name: String,
    @Column(name = "balance", nullable = false)
    @Comment("잔액")
    var balance: Long = 0,
    @Version
    @Column(name = "version", nullable = false)
    @Comment("낙관적락을 위한 버전")
    val version: Long = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun charge(amount: Long) {
        if (amount < MINIMUM_BALANCE) throw BusinessException(ErrorCode.USER_BALANCE_BELOW_MINIMUM)
        if (balance + amount >= MAXIMUM_BALANCE) throw BusinessException(ErrorCode.USER_BALANCE_EXCEEDS_LIMIT)
        balance += amount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()

    fun use(amount: Long) {
        if (balance - amount < 0) throw BusinessException(ErrorCode.INSUFFICIENT_BALANCE)
        balance -= amount
    }

    companion object {
        const val MINIMUM_BALANCE = 10_000L
        const val MAXIMUM_BALANCE = 10_000_000L
    }
}
