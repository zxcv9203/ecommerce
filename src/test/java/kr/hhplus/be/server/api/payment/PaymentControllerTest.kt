package kr.hhplus.be.server.api.payment

import kr.hhplus.be.server.api.payment.request.PaymentRequest
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.order.JpaOrderItemRepository
import kr.hhplus.be.server.infrastructure.persistence.order.JpaOrderRepository
import kr.hhplus.be.server.infrastructure.persistence.product.JpaProductRepository
import kr.hhplus.be.server.infrastructure.persistence.user.JpaUserRepository
import kr.hhplus.be.server.stub.OrderFixture
import kr.hhplus.be.server.stub.ProductFixture
import kr.hhplus.be.server.stub.UserFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.CompletableFuture

class PaymentControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository

    @Autowired
    private lateinit var jpaOrderRepository: JpaOrderRepository

    @Autowired
    private lateinit var jpaOrderItemRepository: JpaOrderItemRepository

    @Autowired
    private lateinit var jpaProductRepository: JpaProductRepository

    @BeforeEach
    fun setUp() {
        val user = UserFixture.create(id = 0L, balance = 10000)
        jpaUserRepository.saveAndFlush(user)
        val user2 = UserFixture.create(id = 0L, balance = 10000)
        jpaUserRepository.saveAndFlush(user2)

        val products =
            listOf(
                ProductFixture.create(id = 0L, name = "Product 1", stock = 10, price = 1000),
                ProductFixture.create(id = 0L, name = "Product 2", stock = 5, price = 2000),
                ProductFixture.create(id = 0L, name = "Product 3", stock = 0, price = 3000),
                ProductFixture.create(id = 0L, name = "Product 4", stock = 1, price = 100000),
                ProductFixture.create(id = 0L, name = "Product 5", stock = 1, price = 1000),
            )
        jpaProductRepository.saveAllAndFlush(products)

        val pendingOrder = OrderFixture.create(id = 0L, user = user, status = OrderStatus.PENDING, totalPrice = 1000)
        jpaOrderRepository.saveAndFlush(pendingOrder)

        val completedOrder =
            OrderFixture.create(id = 0L, user = user, status = OrderStatus.CONFIRMED, totalPrice = 2000)
        jpaOrderRepository.saveAndFlush(completedOrder)

        val outOfStockOrder = OrderFixture.create(id = 0L, user = user, status = OrderStatus.PENDING, totalPrice = 3000)
        jpaOrderRepository.saveAndFlush(outOfStockOrder)
        val insufficientBalanceOrder =
            OrderFixture.create(id = 0L, user = user, status = OrderStatus.PENDING, totalPrice = 100000)
        jpaOrderRepository.saveAndFlush(insufficientBalanceOrder)

        val product5User1Order =
            OrderFixture.create(id = 0L, user = user, status = OrderStatus.PENDING, totalPrice = 1000)
        jpaOrderRepository.saveAndFlush(product5User1Order)
        val product5User2Order =
            OrderFixture.create(id = 0L, user = user2, status = OrderStatus.PENDING, totalPrice = 1000)
        jpaOrderRepository.saveAndFlush(product5User2Order)

        val orderItems =
            listOf(
                OrderFixture.createOrderItem(order = pendingOrder, productId = products[0].id, quantity = 2),
                OrderFixture.createOrderItem(order = pendingOrder, productId = products[1].id, quantity = 1),
                OrderFixture.createOrderItem(order = outOfStockOrder, productId = products[2].id, quantity = 1),
                OrderFixture.createOrderItem(
                    order = insufficientBalanceOrder,
                    productId = products[3].id,
                    quantity = 1,
                ),
                OrderFixture.createOrderItem(
                    order = product5User1Order,
                    productId = products[4].id,
                    quantity = 1,
                ),
                OrderFixture.createOrderItem(
                    order = product5User2Order,
                    productId = products[4].id,
                    quantity = 1,
                ),
            )
        jpaOrderItemRepository.saveAllAndFlush(orderItems)
    }

    @Nested
    @DisplayName("결제 API")
    inner class Payment {
        @Test
        @DisplayName("[성공] 결제가 성공하면 200 반환")
        fun testPaymentSuccess() {
            val request = PaymentRequest(userId = 1L, orderId = 1L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.PAYMENT_COMPLETED.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PAYMENT_COMPLETED.message))
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 사용자로 요청하면 404 반환")
        fun testPaymentFailUserNotFound() {
            val request = PaymentRequest(userId = 0L, orderId = 1L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 주문이 존재하지 않으면 404 반환")
        fun testPaymentFailOrderNotFound() {
            val request = PaymentRequest(userId = 1L, orderId = 0L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value(ErrorCode.ORDER_NOT_FOUND.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ORDER_NOT_FOUND.message))
        }

        @Test
        @DisplayName("[실패] 주문 상태가 결제 대기가 아닌 경우 400 반환")
        fun testPaymentFailOrderNotPending() {
            val request = PaymentRequest(userId = 1L, orderId = 2L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.ORDER_ALREADY_PROCESSED.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ORDER_ALREADY_PROCESSED.message))
        }

        @Test
        @DisplayName("[실패] 재고가 부족한 경우 400 반환")
        fun testPaymentFailOutOfStock() {
            val request = PaymentRequest(userId = 1L, orderId = 3L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_OUT_OF_STOCK.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_OUT_OF_STOCK.message))
        }

        @Test
        @DisplayName("[실패] 잔액이 부족한 경우 400 반환")
        fun testPaymentFailInsufficientBalance() {
            val request = PaymentRequest(userId = 1L, orderId = 4L)

            mockMvc
                .perform(
                    post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.code").value(ErrorCode.INSUFFICIENT_BALANCE.status.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INSUFFICIENT_BALANCE.message))
        }

        @Test
        @DisplayName("[동시성] 동시에 같은 결제 요청이 5번 들어오면 1번만 성공하고 나머지는 실패")
        fun testPaymentConcurrency() {
            val request = PaymentRequest(userId = 1L, orderId = 1L)

            val result =
                ConcurrentTestHelper.executeAsyncTasks(5) {
                    val httpResponse =
                        mockMvc
                            .perform(
                                post("/api/v1/payments")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)),
                            ).andReturn()
                    if (httpResponse.response.status != 200) {
                        throw RuntimeException("Failed to make payment")
                    }
                }

            val successCount = result.count { it }
            val failCount = result.count { !it }

            assertThat(successCount).isEqualTo(1)
            assertThat(failCount).isEqualTo(4)
        }

        @Test
        @DisplayName("[동시성] 동시에 다른 결제 요청이 들어왔을 때 재고가 부족해지는 경우 1번만 성공하고 나머지는 실패")
        fun testPaymentConcurrencyOutOfStock() {
            val requestUser1 = PaymentRequest(userId = 1L, orderId = 5L)
            val requestUser2 = PaymentRequest(userId = 2L, orderId = 6L)

            val tasks =
                listOf(
                    CompletableFuture.runAsync {
                        mockMvc.perform(
                            post("/api/v1/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestUser1)),
                        )
                    },
                    CompletableFuture.runAsync {
                        mockMvc.perform(
                            post("/api/v1/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestUser2)),
                        )
                    },
                )
            CompletableFuture.allOf(*tasks.toTypedArray()).join()

            var confirmCount = 0
            var pendingCount = 0

            jpaOrderRepository.findById(5L).orElseThrow().let {
                if (it.status == OrderStatus.CONFIRMED) {
                    confirmCount++
                } else {
                    pendingCount++
                }
            }
            jpaOrderRepository.findById(6L).orElseThrow().let {
                if (it.status == OrderStatus.CONFIRMED) {
                    confirmCount++
                } else {
                    pendingCount++
                }
            }

            assertThat(confirmCount).isEqualTo(1)
            assertThat(pendingCount).isEqualTo(1)
            val updatedProduct = jpaProductRepository.findById(5L).orElseThrow()
            assertThat(updatedProduct.stock).isEqualTo(0)
        }
    }
}
