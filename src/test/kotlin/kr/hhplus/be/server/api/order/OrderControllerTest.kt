package kr.hhplus.be.server.api.order

import kr.hhplus.be.server.api.order.request.OrderItemRequest
import kr.hhplus.be.server.api.order.request.OrderRequest
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponPolicyRepository
import kr.hhplus.be.server.infrastructure.persistence.coupon.DataJpaCouponRepository
import kr.hhplus.be.server.infrastructure.persistence.product.DataJpaProductRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
import kr.hhplus.be.server.stub.CouponFixture
import kr.hhplus.be.server.stub.ProductFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrderControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaCouponPolicyRepository: DataJpaCouponPolicyRepository

    @Autowired
    private lateinit var dataJpaCouponRepository: DataJpaCouponRepository

    @Autowired
    private lateinit var dataJpaProductRepository: DataJpaProductRepository

    @BeforeEach
    fun setUp() {
        val users = (1..11L).map { UserFixture.create(id = 0L, name = "user $it") }
        dataJpaUserRepository.saveAllAndFlush(users)

        val couponPolicy = CouponFixture.createPolicy(id = 0L, currentCount = 0)
        dataJpaCouponPolicyRepository.saveAndFlush(couponPolicy)

        val coupon = CouponFixture.create(policy = couponPolicy, user = users[0])
        val usedCoupon = CouponFixture.create(policy = couponPolicy, user = users[1], status = CouponStatus.USED)
        dataJpaCouponRepository.saveAndFlush(coupon)
        dataJpaCouponRepository.saveAndFlush(usedCoupon)

        val products =
            listOf(
                ProductFixture.create(id = 0L, name = "Product 1", stock = 10, price = 1000),
                ProductFixture.create(id = 0L, name = "Product 2", stock = 5, price = 2000),
            )
        dataJpaProductRepository.saveAllAndFlush(products)
    }

    @Nested
    @DisplayName("주문 생성")
    inner class Order {
        @Test
        @DisplayName("[성공] 쿠폰 적용 주문 생성 성공")
        fun testCreateOrderSuccess() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.code").value(SuccessCode.ORDER_CREATED.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_CREATED.message))
                .andExpect(jsonPath("$.data.orderId").value(1L))
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용하지 않은 주문 생성 성공")
        fun testCreateOrderWithoutCouponSuccess() {
            val userId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = null,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.code").value(SuccessCode.ORDER_CREATED.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_CREATED.message))
                .andExpect(jsonPath("$.data.orderId").value(1L))
        }

        @Test
        @DisplayName("[실패] 사용자 인증 정보가 유효하지 않은 경우 401 에러 발생")
        fun testFailWhenUnauthorized() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isUnauthorized)
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.message))
        }

        @Test
        @DisplayName("[실패] 인증된 사용자와 전달한 사용자가 다른 경우 403 에러 발생")
        fun testFailWhenForbidden() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, 2L)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isForbidden)
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.message))
        }

        @Test
        @DisplayName("[동시성] 쿠폰 동시 적용 주문 생성 테스트")
        fun testCreateOrderWithConcurrency() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            val result =
                ConcurrentTestHelper.executeAsyncTasks(10) {
                    val httpResponse =
                        mockMvc
                            .perform(
                                post("/api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header(HttpHeaders.AUTHORIZATION, userId)
                                    .content(objectMapper.writeValueAsString(request)),
                            ).andReturn()
                    if (httpResponse.response.status != 201) {
                        throw RuntimeException("Fail")
                    }
                }

            val successCount = result.count { it }
            val failCount = result.size - successCount

            assertThat(successCount).isEqualTo(1)
            assertThat(failCount).isEqualTo(9)
        }

        @Test
        @DisplayName("[실패] 사용자가 존재하지 않는 경우 404 에러 발생")
        fun testFailWhenUserNotFound() {
            val userId = 0L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 전달받은 상품이 존재하지 않는 경우 404 에러 발생")
        fun testFailWhenProductNotFound() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 3L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰을 전달받았을 때 전달받은 쿠폰이 존재하지 않는 경우 404 에러 발생")
        fun testFailWhenCouponNotFound() {
            val userId = 1L
            val couponId = 0L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 쿠폰이 이미 사용된 경우 400 에러 발생")
        fun testFailWhenCouponAlreadyUsed() {
            val userId = 2L
            val couponId = 2L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_ALREADY_USED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_ALREADY_USED.message))
        }

        @Test
        @DisplayName("[실패] 주문할 상품의 재고가 부족한 경우 400 에러 발생")
        fun testFailWhenProductOutOfStock() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 11),
                            OrderItemRequest(productId = 2L, quantity = 6),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_OUT_OF_STOCK.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_OUT_OF_STOCK.message))
        }

        @Test
        @DisplayName("[실패] 주문할 상품의 가격이 쿠폰 할인 금액보다 적은 경우 400 에러 발생")
        fun testFailWhenProductPriceLessThanCouponDiscount() {
            val userId = 1L
            val couponId = 1L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.ORDER_AMOUNT_INVALID.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ORDER_AMOUNT_INVALID.message))
        }

        @Test
        @DisplayName("[실패] 내가 보유한 쿠폰이 아닌 경우 400 에러 발생")
        fun testFailWhenNotMyCoupon() {
            val userId = 1L
            val couponId = 2L

            val request =
                OrderRequest(
                    userId = userId,
                    items =
                        listOf(
                            OrderItemRequest(productId = 1L, quantity = 2),
                            OrderItemRequest(productId = 2L, quantity = 1),
                        ),
                    couponId = couponId,
                )

            mockMvc
                .perform(
                    post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userId)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.COUPON_OWNER_MISMATCH.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.COUPON_OWNER_MISMATCH.message))
        }
    }
}
