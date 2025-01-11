package kr.hhplus.be.server.infrastructure.persistence.coupon

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.coupon.CouponPolicy
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface JpaCouponPolicyRepository :
    CouponPolicyRepository,
    JpaRepository<CouponPolicy, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM CouponPolicy cp WHERE cp.id = :id")
    override fun findByIdWithLock(id: Long): CouponPolicy?
}
