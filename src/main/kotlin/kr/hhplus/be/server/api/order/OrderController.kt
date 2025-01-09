package kr.hhplus.be.server.api.order

import kr.hhplus.be.server.api.order.request.OrderRequest
import kr.hhplus.be.server.api.order.response.OrderResponse
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController : OrderApi {
    @PostMapping
    override fun order(
        @RequestBody request: OrderRequest,
    ): ResponseEntity<CustomResponse<OrderResponse>> {
        if (request.userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val productStock =
            mapOf(
                1L to 10,
                2L to 5,
                3L to 0,
            )
        val productPrices =
            mapOf(
                1L to 10000,
                2L to 5000,
                3L to 1000,
            )

        val validCouponId = 1L
        val usedCouponId = 2L
        var totalPrice = 0

        when (request.couponId) {
            validCouponId -> totalPrice -= 5000
            usedCouponId -> throw BusinessException(ErrorCode.COUPON_ALREADY_USED)
            else -> if (request.couponId != null) throw BusinessException(ErrorCode.COUPON_NOT_FOUND)
        }

        for (item in request.items) {
            val stock = productStock[item.productId] ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
            if (stock < item.quantity) {
                throw BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK)
            }
            val price = productPrices[item.productId] ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
            totalPrice += price * item.quantity
        }

        if (totalPrice <= 0) {
            throw BusinessException(ErrorCode.ORDER_AMOUNT_INVALID)
        }

        val response =
            OrderResponse(
                orderId = 1L,
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CustomResponse.success(SuccessCode.ORDER_CREATED, response))
    }
}
