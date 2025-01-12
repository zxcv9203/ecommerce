package kr.hhplus.be.server.domain.order

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.stub.OrderFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OrderTest {
    @Nested
    @DisplayName("결제 대기 중인 주문인지 확인")
    inner class EnsureNotPaid {
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = ["CONFIRMED"])
        @DisplayName("[실패] 결제 대기인 주문이 아니라면 BusinessException 발생")
        fun testEnsureNotPaidFail(status: String) {
            val orderStatus = OrderStatus.valueOf(status)
            val order = OrderFixture.create(status = orderStatus)

            assertThatThrownBy { order.ensureNotPaid() }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.ORDER_ALREADY_PROCESSED)
        }
    }

    @Nested
    @DisplayName("주문 확정")
    inner class Confirm {
        @Test
        @DisplayName("[성공] 주문 상태가 CONFIRMED로 변경된다.")
        fun testConfirmSuccess() {
            val order = OrderFixture.create(status = OrderStatus.PENDING)

            order.confirm()

            assertThat(order.status).isEqualTo(OrderStatus.CONFIRMED)
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = ["CONFIRMED"])
        @DisplayName("[실패] 이미 결제된 주문인 경우 BusinessException 발생")
        fun testConfirmFail(status: String) {
            val orderStatus = OrderStatus.valueOf(status)
            val order = OrderFixture.create(status = orderStatus)

            assertThatThrownBy { order.confirm() }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("code", ErrorCode.ORDER_ALREADY_PROCESSED)
        }
    }
}
