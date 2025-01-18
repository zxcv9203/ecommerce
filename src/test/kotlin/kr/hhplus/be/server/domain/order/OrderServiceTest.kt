package kr.hhplus.be.server.domain.order

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OrderServiceTest {
    @InjectMockKs
    private lateinit var orderService: OrderService

    @MockK
    private lateinit var orderRepository: OrderRepository

    @MockK
    private lateinit var orderItemRepository: OrderItemRepository

    @Nested
    @DisplayName("주문 ID와 사용자 ID로 주문 조회 (Lock 사용)")
    inner class FindByOrderIdAndUserIdWithLock {
        @Test
        @DisplayName("[실패] 사용자 ID와 주문 ID로 주문을 찾을 수 없는 경우 BusinessException 발생")
        fun testFailWhenOrderNotFound() {
            val orderId = 1L
            val userId = 1L

            every { orderRepository.findByIdAndUserIdWithLock(orderId, userId) } returns null

            assertThatThrownBy { orderService.getByIdAndUserIdWithLock(orderId, userId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.ORDER_NOT_FOUND)
        }
    }
}
