package kr.hhplus.be.server.api.product

import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.application.product.info.ProductInfo
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.infrastructure.persistence.order.DataJpaOrderRepository
import kr.hhplus.be.server.infrastructure.persistence.order.DataJpaOrderItemRepository
import kr.hhplus.be.server.infrastructure.persistence.product.DataJpaProductRepository
import kr.hhplus.be.server.stub.OrderFixture
import kr.hhplus.be.server.stub.ProductFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProductControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var dataJpaProductRepository: DataJpaProductRepository

    @Autowired
    private lateinit var dataJpaOrderRepository: DataJpaOrderRepository

    @Autowired
    private lateinit var dataJpaOrderItemRepository: DataJpaOrderItemRepository

    @BeforeEach
    fun setUp() {
        val products =
            (1..11L)
                .map {
                    ProductFixture.create(
                        name = "상품 $it",
                        price = 1000 * it,
                        stock = 10,
                    )
                }
        dataJpaProductRepository.saveAll(products)
        val confirmedOrders =
            (1..5L).map {
                OrderFixture.create(
                    status = OrderStatus.CONFIRMED,
                )
            }
        dataJpaOrderRepository.saveAllAndFlush(confirmedOrders)

        val pendingOrders =
            (6..7L).map {
                OrderFixture.create(
                    status = OrderStatus.PENDING,
                )
            }
        dataJpaOrderRepository.saveAllAndFlush(pendingOrders)

        val orderItems =
            listOf(
                OrderFixture.createOrderItem(order = confirmedOrders[0], productId = products[0].id, quantity = 3),
                OrderFixture.createOrderItem(order = confirmedOrders[1], productId = products[1].id, quantity = 2),
                OrderFixture.createOrderItem(order = confirmedOrders[2], productId = products[2].id, quantity = 5),
                OrderFixture.createOrderItem(order = confirmedOrders[3], productId = products[3].id, quantity = 7),
                OrderFixture.createOrderItem(order = confirmedOrders[4], productId = products[4].id, quantity = 1),
                OrderFixture.createOrderItem(order = confirmedOrders[4], productId = products[5].id, quantity = 2),
            )
        dataJpaOrderItemRepository.saveAllAndFlush(orderItems)

        val pendingOrderItems =
            listOf(
                OrderFixture.createOrderItem(order = pendingOrders[0], productId = products[4].id, quantity = 4),
                OrderFixture.createOrderItem(order = pendingOrders[1], productId = products[5].id, quantity = 6),
            )
        dataJpaOrderItemRepository.saveAllAndFlush(pendingOrderItems)
    }

    @Nested
    @DisplayName("상품 목록 조회 API")
    inner class FindAll {
        @Test
        @DisplayName("[성공]상품 목록을 조회한다. (다음 페이지가 존재하는 경우)")
        fun testFindAllNextPage() {
            mockMvc
                .perform(
                    get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_QUERY.message))
                .andExpect(jsonPath("$.data.products").isArray)
                .andExpect(jsonPath("$.data.products", Matchers.hasSize<ProductInfo>(10)))
                .andExpect(jsonPath("$.data.hasNext").value(true))
        }

        @Test
        @DisplayName("[성공]상품 목록을 조회한다. (다음 페이지가 존재하지 않는 경우)")
        fun findAllLastPage() {
            mockMvc
                .perform(
                    get("/api/v1/products")
                        .param("page", "1")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_QUERY.message))
                .andExpect(jsonPath("$.data.products").isArray)
                .andExpect(jsonPath("$.data.products", Matchers.hasSize<ProductInfo>(1)))
                .andExpect(jsonPath("$.data.hasNext").value(false))
        }
    }

    @Nested
    @DisplayName("인기 상품 목록 조회 API")
    inner class FindPopularProducts {
        @Test
        @DisplayName("[성공]인기 상품 목록을 조회한다.")
        fun testFindPopularProducts() {
            mockMvc
                .perform(get("/api/v1/popular-products"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.POPULAR_PRODUCT_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.POPULAR_PRODUCT_QUERY.message))
                .andExpect(jsonPath("$.data.products").isArray)
                .andExpect(jsonPath("$.data.products", Matchers.hasSize<PopularProductInfo>(5)))
        }
    }
}
