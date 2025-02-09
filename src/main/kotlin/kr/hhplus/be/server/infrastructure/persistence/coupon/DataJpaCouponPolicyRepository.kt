package kr.hhplus.be.server.infrastructure.persistence.coupon

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.coupon.CouponPolicy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface DataJpaCouponPolicyRepository : JpaRepository<CouponPolicy, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM CouponPolicy cp WHERE cp.id = :id")
    fun findByIdWithLock(id: Long): CouponPolicy?

    @Query("SELECT cp FROM CouponPolicy cp WHERE cp.currentCount < cp.totalCount")
    fun findIssuableCouponPolicy(): List<CouponPolicy>

}
