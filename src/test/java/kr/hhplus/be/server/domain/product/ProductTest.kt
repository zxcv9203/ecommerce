package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.ProductFixture
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProductTest {
    @Nested
    @DisplayName("충분한 재고가 존재하는지 확인")
    inner class EnsureAvailableStock {
        @Test
        @DisplayName("[실패] 재고가 충분하지 않은 경우 BusinessException 발생")
        fun test_fail_when_stock_is_not_enough() {
            val product = ProductFixture.create()
            val orderQuantity = product.stock + 1

            assertThatThrownBy { product.ensureAvailableStock(orderQuantity) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.PRODUCT_OUT_OF_STOCK)
        }
    }
}
