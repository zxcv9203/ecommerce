package kr.hhplus.be.server.application.coupon

import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.infrastructure.queue.coupon.RedisCouponIssueProcessor
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CouponIssueSchedulerTest : IntegrationTest() {
    @Autowired
    private lateinit var couponIssueScheduler: CouponIssueScheduler

    @Autowired
    private lateinit var jpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaCouponPolicyRepository: DataJpaCouponPolicyRepository

    @Autowired
    private lateinit var redisCouponIssueProcessor: RedisCouponIssueProcessor

    @Autowired
    private lateinit var dataJpaCouponRepository: DataJpaCouponRepository

    @Nested
    @DisplayName("요청한 쿠폰을 발급 처리하는 스케줄러")
    inner class IssueCoupon {
        @Test
        @DisplayName("[성공] 사용자 3명이 남은 수량이 2개인 쿠폰을 발급 요청한 경우 2명은 성공하고 1명은 실패한다.")
        fun test_issueCoupon() {
            val users = (1..3).map { UserFixture.create(id = 0L, name = "user $it") }
            jpaUserRepository.saveAllAndFlush(users)
            val couponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 2, currentCount = 0)
            dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)
            users.forEach {
                redisCouponIssueProcessor.pushCouponIssueQueue(it.id, couponPolicy.id)
            }

            couponIssueScheduler.issueCoupon()

            val coupons = dataJpaCouponRepository.findAll()

            assertThat(coupons).hasSize(2)

            val a = redisCouponIssueProcessor.isAlreadyIssued(1L, 1L)
            val b = redisCouponIssueProcessor.isAlreadyIssued(2L, 1L)
            assertThat(a).isTrue()
            assertThat(b).isTrue()
        }

        @Test
        @DisplayName("[실패] 사용자 3명이 이미 발급한 쿠폰을 요청한 경우 중복 발급을 방지한다.")
        fun test_duplicate() {
            val users = (1..3).map { UserFixture.create(id = 0L, name = "user $it") }
            jpaUserRepository.saveAllAndFlush(users)
            val couponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 10, currentCount = 0)
            dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)
            users.forEach {
                redisCouponIssueProcessor.pushCouponIssueQueue(it.id, couponPolicy.id)
            }
            couponIssueScheduler.issueCoupon()

            users.forEach {
                redisCouponIssueProcessor.pushCouponIssueQueue(it.id, couponPolicy.id)
            }
            couponIssueScheduler.issueCoupon()

            val coupons = dataJpaCouponRepository.findAll()

            assertThat(coupons).hasSize(3)

            val a = redisCouponIssueProcessor.isAlreadyIssued(1L, 1L)
            val b = redisCouponIssueProcessor.isAlreadyIssued(2L, 1L)
            val c = redisCouponIssueProcessor.isAlreadyIssued(3L, 1L)
            assertThat(a).isTrue()
            assertThat(b).isTrue()
            assertThat(c).isTrue()
        }

        @Test
        @DisplayName("[동시성] 스케줄러가 동시에 3개가 도는 경우 쿠폰 3개가 발급되는지 확인한다.")
        fun test_concurrency() {
            val users = (1..3).map { UserFixture.create(id = 0L, name = "user $it") }
            jpaUserRepository.saveAllAndFlush(users)
            val couponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 10, currentCount = 0)
            dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)
            users.forEach {
                redisCouponIssueProcessor.pushCouponIssueQueue(it.id, couponPolicy.id)
            }

            ConcurrentTestHelper.executeAsyncTasks(3) {
                couponIssueScheduler.issueCoupon()
            }

            val coupons = dataJpaCouponRepository.findAll()

            assertThat(coupons).hasSize(3)

            val a = redisCouponIssueProcessor.isAlreadyIssued(1L, 1L)
            val b = redisCouponIssueProcessor.isAlreadyIssued(2L, 1L)
            val c = redisCouponIssueProcessor.isAlreadyIssued(3L, 1L)
            assertThat(a).isTrue()
            assertThat(b).isTrue()
            assertThat(c).isTrue()
        }
    }
}
