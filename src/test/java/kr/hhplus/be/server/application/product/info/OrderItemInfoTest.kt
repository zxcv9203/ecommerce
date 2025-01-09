package kr.hhplus.be.server.application.product.info

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrderItemInfoTest {
    @Nested
    @DisplayName("주문 상품의 가격 총합을 계산")
    inner class GetTotalPrice {
        @Test
        @DisplayName("[성공] 주문 상품의 가격 총합을 계산")
        fun test_getTotalPrice_success() {
            val orderItemInfoList =
                listOf(
                    OrderItemInfo(1, 1000, 2),
                    OrderItemInfo(2, 2000, 3),
                )

            val totalPrice = orderItemInfoList.getTotalPrice()

            assertThat(totalPrice).isEqualTo(8000)
        }
    }
}
