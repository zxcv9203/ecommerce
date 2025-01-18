package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class IntegrationCouponServiceTest : IntegrationTest() {
    @Autowired
    private lateinit var couponService: CouponService

    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaCouponPolicyRepository: DataJpaCouponPolicyRepository

    @Autowired
    private lateinit var dataJpaCouponRepository: DataJpaCouponRepository

    @BeforeEach
    fun setUp() {
        val users = (1..11L).map { UserFixture.create(id = 0L, name = "user $it") }
        dataJpaUserRepository.saveAllAndFlush(users)

        val couponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 10, currentCount = 0)
        dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)
    }

    @Nested
    @DisplayName("쿠폰 발급 동시성 테스트")
    inner class IssueCouponConcurrencyTest {
        @Test
        @DisplayName("[동시성] 동시에 11명의 사용자가 쿠폰을 발급할 때 10번 성공하고 1번 실패해야 한다.")
        fun issueCouponConcurrencyTest() {
            val couponPolicyId = 1L

            val result =
                ConcurrentTestHelper.executeAsyncTasksWithIndex(11) {
                    val user = dataJpaUserRepository.findByIdOrNull(it.toLong() + 1)
                    couponService.issue(user!!, couponPolicyId)
                }

            val successCount = result.count { it }
            val failCount = result.count { !it }
            val issuedCoupons = dataJpaCouponRepository.findAll()

            assertThat(successCount).isEqualTo(10)
            assertThat(failCount).isEqualTo(1)
            assertThat(issuedCoupons.size).isEqualTo(successCount)
        }
    }
}
