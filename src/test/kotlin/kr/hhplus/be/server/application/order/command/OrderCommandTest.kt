package kr.hhplus.be.server.application.order.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrderCommandTest {
    @Nested
    @DisplayName("주문 상품 목록에서 ID를 추출한 후 오름차순으로 정렬")
    inner class ToSortedProductIds {
        @Test
        @DisplayName("[성공] 주문 상품 목록에서 ID를 추출한 후 오름차순 정렬에 성공한다.")
        fun test_ToSortedProductIds() {
            val command =
                listOf(
                    OrderItemCommand(3L, 10),
                    OrderItemCommand(2L, 22),
                    OrderItemCommand(1L, 22),
                    OrderItemCommand(5L, 22),
                    OrderItemCommand(4L, 22),
                )
            val want = listOf(1L, 2L, 3L, 4L, 5L)

            val got = command.toSortedProductIds()

            assertThat(got).isEqualTo(want)
        }
    }
}
