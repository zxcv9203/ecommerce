package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
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
    var version: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
