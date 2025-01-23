package kr.hhplus.be.server.infrastructure.persistence.coupon

import kr.hhplus.be.server.domain.coupon.CouponPolicy
import kr.hhplus.be.server.domain.coupon.CouponPolicyRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaCouponPolicyRepository(
    private val dataJpaCouponPolicyRepository: DataJpaCouponPolicyRepository,
) : CouponPolicyRepository {
    override fun findById(id: Long): CouponPolicy? = dataJpaCouponPolicyRepository.findByIdOrNull(id)
}
