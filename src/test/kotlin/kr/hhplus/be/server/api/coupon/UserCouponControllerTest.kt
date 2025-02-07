package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.application.coupon.info.CouponInfo
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
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

        dataJpaCouponRepository.saveAndFlush(CouponFixture.create(id = 0L, user = users[0], policy = couponPolicy))
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
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.COUPON_ISSUE_SUCCESS.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COUPON_ISSUE_SUCCESS.message))
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

        @Nested
        @DisplayName("쿠폰 조회")
        inner class FindAll {
            @Test
            @DisplayName("[성공] 사용자의 쿠폰 목록을 조회한다.")
            fun test_findAll() {
                val userId = 1L
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
}
