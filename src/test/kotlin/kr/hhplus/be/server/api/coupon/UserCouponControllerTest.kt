package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.application.coupon.info.CouponInfo
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class UserCouponControllerTest : IntegrationTest() {
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

        val couponPolicy = CouponFixture.createPolicy(id = 0L, currentCount = 0)
        val fullIssueCouponPolicy = CouponFixture.createPolicy(id = 0L, totalCount = 1, currentCount = 1)
        val endDateCouponPolicy = CouponFixture.createPolicy(id = 0L, endTime = LocalDateTime.now().minusDays(1))
        val notStartedCouponPolicy = CouponFixture.createPolicy(id = 0L, startTime = LocalDateTime.now().plusDays(1))
        dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)
        dataJpaCouponPolicyRepository.saveAndFlush(fullIssueCouponPolicy)
        dataJpaCouponPolicyRepository.saveAndFlush(endDateCouponPolicy)
        dataJpaCouponPolicyRepository.saveAndFlush(notStartedCouponPolicy)
    }

    @Nested
    @DisplayName("쿠폰 발급")
    inner class Issue {
        @Test
        @DisplayName("[성공] 쿠폰 발급에 성공한다.")
        fun test_issueCoupon() {
            val userId = 1L
            val request = IssueCouponRequest(1L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.code").value(SuccessCode.COUPON_ISSUE_SUCCESS.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COUPON_ISSUE_SUCCESS.message))

            val coupon =
                dataJpaCouponRepository.findByIdOrNull(1L)
                    ?: throw AssertionError("Coupon not found")

            assertThat(coupon.user.id).isEqualTo(userId)
        }

        @Test
        @DisplayName("[실패] 인증 헤더의 사용자 ID가 유효하지 않으면 401 반환")
        fun test_fail_when_invalid_user_id() {
            val userId = 1L
            val request = IssueCouponRequest(1L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.message))
        }

        @Test
        @DisplayName("[실패] 인증 아이디와 사용자 아이디가 다르면 403 반환")
        fun test_fail_when_user_id_and_auth_id_diff() {
            val userId = 1L
            val request = IssueCouponRequest(1L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, 2L)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isForbidden)
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.message))
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 사용자인 경우 404 반환")
        fun test_fail_when_user_not_found() {
            val userId = 0L
            val request = IssueCouponRequest(1L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰 정책이 존재하지 않는 경우 404 반환")
        fun test_fail_when_coupon_policy_not_found() {
            val userId = 1L
            val request = IssueCouponRequest(0L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰 정책의 발급 가능한 수량이 없는 경우 400 반환")
        fun test_fail_when_coupon_policy_current_count_is_zero() {
            val userId = 1L

            val request = IssueCouponRequest(2L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_OUT_OF_COUNT.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_OUT_OF_COUNT.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰 정책의 발급 가능한 기간이 지난 경우 400 반환")
        fun test_fail_when_coupon_policy_is_not_available() {
            val userId = 1L

            val request = IssueCouponRequest(3L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_ISSUE_EXPIRED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_ISSUE_EXPIRED.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰 정책의 발급 가능한 기간이 아직 시작되지 않은 경우 400 반환")
        fun test_fail_when_coupon_policy_is_not_started() {
            val userId = 1L

            val request = IssueCouponRequest(4L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_ISSUE_NOT_STARTED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_ISSUE_NOT_STARTED.message))
        }

        @Test
        @DisplayName("[실패] 이미 발급된 쿠폰 정책인 경우 400 반환")
        fun test_fail_when_coupon_already_issued() {
            val userId = 1L
            val request = IssueCouponRequest(1L)

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                )
            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_ALREADY_ISSUED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_ALREADY_ISSUED.message))
        }

        @Test
        @DisplayName("[동시성] 동시에 11명의 사용자가 쿠폰을 발급하면 1명은 실패한다.")
        fun test_concurrency_issue_coupon() {
            val request = IssueCouponRequest(1L)

            val result =
                ConcurrentTestHelper.executeAsyncTasksWithIndex(11) { index ->
                    val httpResponse =
                        mockMvc
                            .perform(
                                post("/api/v1/users/{userId}/coupons", index + 1)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header(HttpHeaders.AUTHORIZATION, index + 1)
                                    .content(objectMapper.writeValueAsString(request)),
                            ).andReturn()
                    if (httpResponse.response.status != HttpStatus.CREATED.value()) {
                        throw RuntimeException("Fail")
                    }
                }

            val successCount = result.count { it }
            val failCount = result.count { !it }

            assertThat(successCount).isEqualTo(10)
            assertThat(failCount).isEqualTo(1)
        }
    }

    @Nested
    @DisplayName("쿠폰 조회")
    inner class FindAll {
        @Test
        @DisplayName("[성공] 사용자의 쿠폰 목록을 조회한다.")
        fun test_findAll() {
            val userId = 1L

            mockMvc
                .perform(
                    post("/api/v1/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(IssueCouponRequest(1L))),
                )
            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/coupons", userId)
                        .header(HttpHeaders.AUTHORIZATION, userId),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.COUPON_LIST_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COUPON_LIST_QUERY.message))
                .andExpect(jsonPath("$.data.coupons", Matchers.hasSize<CouponInfo>(1)))
                .andExpect(jsonPath("$.data.hasNext").value(false))
        }

        @Test
        @DisplayName("[실패] 인증 헤더의 사용자 ID가 유효하지 않으면 401 반환")
        fun test_fail_when_invalid_user_id() {
            val userId = 1L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/coupons", userId),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.message))
        }

        @Test
        @DisplayName("[실패] 인증 아이디와 사용자 아이디가 다르면 403 반환")
        fun test_fail_when_user_id_and_auth_id_diff() {
            val userId = 1L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/coupons", userId)
                        .header(HttpHeaders.AUTHORIZATION, 2L),
                ).andExpect(status().isForbidden)
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.message))
        }

        @Test
        @DisplayName("[실패] 사용자가 존재하지 않는 경우 404 반환")
        fun test_fail_when_user_not_found() {
            val userId = 0L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/coupons", userId)
                        .header(HttpHeaders.AUTHORIZATION, userId),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }
    }
}
