package kr.hhplus.be.server.api.user

import kr.hhplus.be.server.api.user.request.UserBalanceRequest
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaBalanceHistoryRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserBalanceControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaBalanceHistoryRepository: DataJpaBalanceHistoryRepository

    @BeforeEach
    fun setUp() {
        val user = UserFixture.create(id = 0L, balance = 10000)
        dataJpaUserRepository.saveAndFlush(user)
    }

    @Nested
    @DisplayName("잔액 충전")
    inner class ChargeBalance {
        @Test
        @DisplayName("[성공] 잔액 충전에 성공합니다.")
        fun testSuccessChargeBalance() {
            val userId = 1L
            val amount = 10000L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(
                            objectMapper.writeValueAsString(UserBalanceRequest(amount)),
                        ),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.USER_BALANCE_CHARGE.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_BALANCE_CHARGE.message))
                .andExpect(jsonPath("$.data.amount").value(20000))

            val histories = dataJpaBalanceHistoryRepository.findAll()

            assertThat(histories).hasSize(1)
            assertThat(histories[0].user.id).isEqualTo(userId)
            assertThat(histories[0].amount).isEqualTo(amount)
        }

        @Test
        @DisplayName("[실패] 인증 헤더의 사용자 ID가 유효하지 않으면 401 반환")
        fun testFailWhenInvalidAuthId() {
            val userId = 1L
            val amount = 10000L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(UserBalanceRequest(amount)),
                        ),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[실패] 인증 아이디와 사용자 아이디가 다르면 403 반환")
        fun testFailWhenUserIdAndAuthIdDiff() {
            val userId = 1L
            val amount = 10000L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, 2)
                        .content(
                            objectMapper.writeValueAsString(UserBalanceRequest(amount)),
                        ),
                ).andExpect(status().isForbidden)
        }

        @Test
        @DisplayName("[실패] 사용자가 존재하지 않는 경우 404에러 발생")
        fun testFailWhenUserNotFound() {
            val userId = 0L
            val amount = 10000L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(UserBalanceRequest(amount)),
                        ),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 충전 금액이 최소 금액 미만일 경우 400에러 발생")
        fun testFailWhenChargeAmountBelowMinimum() {
            val userId = 1L
            val amount = 9999L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(
                            """
                            {
                                "amount": $amount
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_BALANCE_BELOW_MINIMUM.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_BALANCE_BELOW_MINIMUM.message))
        }

        @Test
        @DisplayName("[실패] 충전 후 잔액이 최대 잔액을 초과할 경우 400에러 발생")
        fun testFailWhenExceedsMaximum() {
            val userId = 1L
            val amount = 10000000L

            mockMvc
                .perform(
                    patch("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(
                            """
                            {
                                "amount": $amount
                            }
                            """.trimIndent(),
                        ),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_BALANCE_EXCEEDS_LIMIT.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_BALANCE_EXCEEDS_LIMIT.message))
        }

        @Test
        @DisplayName("[동시성] 동시에 충전 요청이 5번 들어올 때 충돌이 발생하지 않은 만큼 잔액이 증가합니다.")
        fun testFailWhenConcurrentChargeRequest() {
            val userId = 1L
            val amount = 10000L

            val result =
                ConcurrentTestHelper.executeAsyncTasks(5) {
                    val httpResponse =
                        mockMvc
                            .perform(
                                patch("/api/v1/users/{userId}/balance", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header(HttpHeaders.AUTHORIZATION, userId)
                                    .content(
                                        objectMapper.writeValueAsString(UserBalanceRequest(amount)),
                                    ),
                            ).andReturn()

                    if (httpResponse.response.status != HttpStatus.OK.value()) {
                        throw RuntimeException("Fail")
                    }
                }

            val successCount = result.count { it }

            val user = dataJpaUserRepository.findByIdOrNull(1) ?: throw RuntimeException("User not found")
            assertThat(user.balance).isEqualTo(10000 + successCount * amount)
        }
    }

    @Nested
    @DisplayName("잔액 조회")
    inner class GetBalance {
        @Test
        @DisplayName("[성공] 잔액 조회에 성공합니다.")
        fun testSuccessGetBalance() {
            val userId = 1L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/balance", userId)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.USER_BALANCE_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_BALANCE_QUERY.message))
                .andExpect(jsonPath("$.data.amount").value(10000))
        }

        @Test
        @DisplayName("[실패] 인증 헤더의 사용자 ID가 유효하지 않으면 401 반환")
        fun testFailWhenInvalidAuthId() {
            val userId = 1L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        @DisplayName("[실패] 인증 아이디와 사용자 아이디가 다르면 403 반환")
        fun testFailWhenUserIdAndAuthIdDiff() {
            val userId = 1L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, 2),
                ).andExpect(status().isForbidden)
        }

        @Test
        @DisplayName("[실패] 사용자가 존재하지 않는 경우 404에러 발생")
        fun testFailWhenUserNotFound() {
            val userId = 0L

            mockMvc
                .perform(
                    get("/api/v1/users/{userId}/balance", userId)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }
    }
}
