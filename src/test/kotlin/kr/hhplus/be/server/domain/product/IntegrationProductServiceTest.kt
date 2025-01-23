package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.application.order.command.OrderItemCommand
import kr.hhplus.be.server.helper.ConcurrentTestHelper
import kr.hhplus.be.server.infrastructure.persistence.product.DataJpaProductRepository
import kr.hhplus.be.server.stub.ProductFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IntegrationProductServiceTest : IntegrationTest() {
    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var dataJpaProductRepository: DataJpaProductRepository

    @BeforeEach
    fun setUp() {
        val products = (1..10L).map { ProductFixture.create(id = 0L, stock = 10) }
        dataJpaProductRepository.saveAllAndFlush(products)
    }

    @Nested
    @DisplayName("재고 감소 동시성 테스트")
    inner class ReduceStock {
        @Test
        @DisplayName("[동시성] 상품의 재고를 1개씩 11번 감소시킬때 10번 성공하고 1번 실패해야 한다.")
        fun reduceStockConcurrentTest() {
            val ids =
                (1..10L)
                    .map { OrderItemCommand(it, 1) }
                    .toList()
            val result =
                ConcurrentTestHelper.executeAsyncTasksWithIndex(11) {
                    productService.reduceStock(ids)
                }

            val successCount = result.count { it }
            val failCount = result.count { !it }
            val updatedProducts = dataJpaProductRepository.findAll()

            assertThat(successCount).isEqualTo(10)
            assertThat(failCount).isEqualTo(1)
            assertThat(updatedProducts.all { it.stock == 0 }).isTrue()
        }
    }
}
