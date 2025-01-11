package kr.hhplus.be.server.domain.product

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.hhplus.be.server.application.order.command.OrderItemCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProductServiceTest {
    @InjectMockKs
    private lateinit var productService: ProductService

    @MockK
    private lateinit var productRepository: ProductRepository

    @Nested
    @DisplayName("주문 가능한 상품 목록 조회")
    inner class FindOrderableProductByIds {
        @Test
        @DisplayName("[성공] 모든 제품이 존재하고 재고가 충분한 경우")
        fun test_findOrderableProductByIds() {
            val items =
                listOf(
                    OrderItemCommand(1L, 2),
                    OrderItemCommand(2L, 3),
                )
            val products =
                listOf(
                    ProductFixture.create(id = 1L, stock = 10, price = 1000),
                    ProductFixture.create(id = 2L, stock = 10, price = 2000),
                )

            every { productRepository.findAllByIds(listOf(1L, 2L)) } returns products

            val result = productService.findOrderableProductByIds(items)

            assertThat(result).hasSize(2)
            assertThat(result).extracting("id").containsExactly(1L, 2L)
            assertThat(result).extracting("quantity").containsExactly(2, 3)
            assertThat(result).extracting("price").containsExactly(1000L, 2000L)
        }

        @Test
        @DisplayName("[실패] 제품 중 하나라도 존재하지 않는 경우 BusinessException 발생")
        fun test_fail_when_product_not_found() {
            val items =
                listOf(
                    OrderItemCommand(1L, 2),
                    OrderItemCommand(2L, 3),
                )
            val products =
                listOf(
                    ProductFixture.create(id = 1L, stock = 10, price = 1000),
                )

            every { productRepository.findAllByIds(listOf(1L, 2L)) } returns products

            assertThatThrownBy { productService.findOrderableProductByIds(items) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.PRODUCT_NOT_FOUND)
        }
    }
}
