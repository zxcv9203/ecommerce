package kr.hhplus.be.server.stub

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponDiscountType
import kr.hhplus.be.server.domain.coupon.CouponPolicy
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.user.User
import java.time.LocalDateTime

object CouponFixture {
    fun createPolicy(
        name: String = "Test Policy",
        description: String = "Test Description",
        totalCount: Int = 10,
        currentCount: Int = 5,
        startTime: LocalDateTime = LocalDateTime.now().minusDays(1),
        endTime: LocalDateTime = LocalDateTime.now().plusDays(1),
        discountType: CouponDiscountType = CouponDiscountType.AMOUNT,
        discountAmount: Long = 1000L,
        id: Long = 1,
    ): CouponPolicy =
        CouponPolicy(
            name = name,
            description = description,
            totalCount = totalCount,
            currentCount = currentCount,
            startTime = startTime,
            endTime = endTime,
            discountType = discountType,
            discountAmount = discountAmount,
            id = id,
        )

    fun create(
        policy: CouponPolicy = createPolicy(),
        user: User = UserFixture.create(),
        status: CouponStatus = CouponStatus.ACTIVE,
        order: Order? = null,
        id: Long = 0,
    ) = Coupon(
        policy = policy,
        user = user,
        status = status,
        order = order,
        id = id,
    )
}
