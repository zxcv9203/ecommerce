package kr.hhplus.be.server.domain.coupon

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.order.DataJpaOrderRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.OrderFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired

class IntegrationCouponServiceTest : IntegrationTest() {
    @Autowired
    private lateinit var couponService: CouponService

    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaCouponPolicyRepository: DataJpaCouponPolicyRepository

    @Autowired
    private lateinit var dataJpaCouponRepository: DataJpaCouponRepository

    @Autowired
    private lateinit var dataJpaOrderRepository: DataJpaOrderRepository

    private lateinit var coupon: Coupon

    private lateinit var order: Order

    @BeforeEach
    fun setUp() {
        val user = UserFixture.create(id = 0L, name = "user")
        dataJpaUserRepository.saveAndFlush(user)

        val couponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 10, currentCount = 0)
        dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)

        coupon = CouponFixture.create(id = 0L, policy = couponPolicy)
        dataJpaCouponRepository.saveAndFlush(coupon)

        order = OrderFixture.create(id = 0L, user = user)
        dataJpaOrderRepository.saveAndFlush(order)
    }

    @Nested
    @DisplayName("쿠폰 예약 동시성 테스트")
    inner class Reserve {
        @Test
        @DisplayName("[동시성] 같은 사용자가 5번 주문 건에 대해 쿠폰 예약을 시도할 때 1번만 성공하고 4번 실패한다.")
        fun reserveCouponConcurrencyTest() {
            val result =
                ConcurrentTestHelper.executeAsyncTasksWithIndex(5) {
                    couponService.reserve(coupon = coupon, order = order)
                }

            val successCount = result.count { it }
            val failedCount = result.count { !it }

            assertThat(successCount).isEqualTo(1)
            assertThat(failedCount).isEqualTo(4)
        }
    }
}
