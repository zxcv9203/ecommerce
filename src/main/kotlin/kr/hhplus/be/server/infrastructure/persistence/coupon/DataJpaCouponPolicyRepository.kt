package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.domain.coupon.CouponPolicy
import org.springframework.data.jpa.repository.JpaRepository

interface DataJpaCouponPolicyRepository : JpaRepository<CouponPolicy, Long>
