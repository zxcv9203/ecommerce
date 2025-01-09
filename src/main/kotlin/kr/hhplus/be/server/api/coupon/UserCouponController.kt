package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.api.coupon.response.CouponResponse
import kr.hhplus.be.server.api.coupon.response.CouponsResponse
import kr.hhplus.be.server.application.coupon.CouponUseCase
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.CustomResponse
import kr.hhplus.be.server.domain.coupon.CouponDiscountType
import kr.hhplus.be.server.domain.coupon.CouponStatus
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/{userId}/coupons")
class UserCouponController(
    private val couponUseCase: CouponUseCase,
) : UserCouponApi {
    @GetMapping
    override fun findAll(
        @PathVariable userId: Long,
        pageable: Pageable,
    ): ResponseEntity<CustomResponse<CouponsResponse>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val coupons =
            listOf(
                CouponResponse(
                    1,
                    101,
                    "쿠폰 A",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    5000,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    2,
                    102,
                    "쿠폰 B",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    10,
                    CouponStatus.USED,
                ),
                CouponResponse(
                    3,
                    103,
                    "쿠폰 C",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    3000,
                    CouponStatus.USED,
                ),
                CouponResponse(
                    4,
                    104,
                    "쿠폰 D",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    15,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    5,
                    105,
                    "쿠폰 E",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    7000,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    6,
                    106,
                    "쿠폰 F",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    20,
                    CouponStatus.CANCELLED,
                ),
                CouponResponse(
                    7,
                    107,
                    "쿠폰 G",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    1000,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    8,
                    108,
                    "쿠폰 H",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    5,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    9,
                    109,
                    "쿠폰 I",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    1500,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    10,
                    110,
                    "쿠폰 J",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    25,
                    CouponStatus.ACTIVE,
                ),
                CouponResponse(
                    11,
                    111,
                    "쿠폰 K",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    4500,
                    CouponStatus.ACTIVE,
                ),
            )

        val sortedCoupons =
            if (pageable.sort.isSorted) {
                coupons.sortedWith { a, b ->
                    pageable.sort
                        .map { order ->
                            val property = order.property
                            val direction = order.direction

                            val compareResult =
                                when (property) {
                                    "id" -> a.id.compareTo(b.id)
                                    "policyId" -> a.policyId.compareTo(b.policyId)
                                    "discountValue" -> a.discountValue.compareTo(b.discountValue)
                                    else -> 0
                                }

                            if (direction.isAscending) compareResult else -compareResult
                        }.firstOrNull { it != 0 } ?: 0
                }
            } else {
                coupons
            }

        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(sortedCoupons.size)
        val paginatedCoupons = if (start < sortedCoupons.size) sortedCoupons.subList(start, end) else emptyList()

        val page = PageImpl(paginatedCoupons, pageable, sortedCoupons.size.toLong())

        val response =
            CouponsResponse(
                coupons = page.content,
                hasNext = page.hasNext(),
            )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomResponse.success(SuccessCode.COUPON_LIST_QUERY, response))
    }

    @PostMapping
    override fun issue(
        @PathVariable userId: Long,
        @RequestBody request: IssueCouponRequest,
    ): ResponseEntity<CustomResponse<Unit>> =
        request
            .toCommand(userId)
            .let { couponUseCase.issue(it) }
            .let { CustomResponse.success(SuccessCode.COUPON_ISSUE_SUCCESS, it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
}
