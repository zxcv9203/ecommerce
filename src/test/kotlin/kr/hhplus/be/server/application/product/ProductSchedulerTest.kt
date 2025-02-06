package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.domain.product.PopularProductCacheRepository
import kr.hhplus.be.server.infrastructure.persistence.order.DataJpaOrderItemRepository
import kr.hhplus.be.server.infrastructure.persistence.order.DataJpaOrderRepository
import kr.hhplus.be.server.infrastructure.persistence.product.DataJpaProductRepository
import kr.hhplus.be.server.infrastructure.persistence.user.DataJpaUserRepository
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

class ProductSchedulerTest : IntegrationTest() {
    @Autowired
    private lateinit var productScheduler: ProductScheduler

    @Autowired
    private lateinit var popularProductCacheRepository: PopularProductCacheRepository

    @Autowired
    private lateinit var dataJpaUserRepository: DataJpaUserRepository

    @Autowired
    private lateinit var dataJpaProductRepository: DataJpaProductRepository

    @Autowired
    private lateinit var dataJpaOrderRepository: DataJpaOrderRepository

    @Autowired
    private lateinit var dataJpaOrderItemRepository: DataJpaOrderItemRepository

    @BeforeEach
    fun setUp() {
        val user = UserFixture.create(id = 0L, name = "user 1")
        dataJpaUserRepository.saveAndFlush(user)

        val product =
            ProductFixture.create(id = 0L, name = "상품 1", stock = 10, price = 1000)
        dataJpaProductRepository.saveAndFlush(product)

        val completedOrder =
            OrderFixture.create(
                id = 0L,
                user = user,
                status = OrderStatus.CONFIRMED,
                totalPrice = 1000,
                discountPrice = 1000,
            )
        dataJpaOrderRepository.saveAndFlush(completedOrder)

        val orderItem =
            OrderFixture.createOrderItem(
                id = 0L,
                order = completedOrder,
                productId = product.id,
                quantity = 1,
            )
        dataJpaOrderItemRepository.saveAndFlush(orderItem)
    }

    @Nested
    @DisplayName("인기 상품 캐시 저장소 업데이트")
    inner class UpdatePopularProductCache {
        @Test
        @DisplayName("[성공] 인기 상품을 캐시 저장소에 업데이트 한다.")
        fun testUpdatePopularProductCache() {
            val want =
                listOf(
                    PopularProductInfo(
                        id = 1,
                        name = "상품 1",
                        price = 1000,
                        totalSales = 1,
                    ),
                )

            productScheduler.updatePopularProducts()

            val got = popularProductCacheRepository.findAll()
            assertThat(got).hasSize(1)
            assertThat(got).usingRecursiveComparison().isEqualTo(want)
        }
    }
}
