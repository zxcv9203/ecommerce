package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.api.coupon.response.CouponResponse
import kr.hhplus.be.server.api.coupon.response.CouponsResponse
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
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/users/{userId}/coupons")
class UserCouponController : UserCouponApi {
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
                    LocalDateTime.of(2025, 1, 1, 10, 0),
                    LocalDateTime.of(2025, 1, 15, 23, 59),
                ),
                CouponResponse(
                    2,
                    102,
                    "쿠폰 B",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    10,
                    CouponStatus.USED,
                    LocalDateTime.of(2024, 12, 20, 10, 0),
                    LocalDateTime.of(2024, 12, 31, 23, 59),
                ),
                CouponResponse(
                    3,
                    103,
                    "쿠폰 C",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    3000,
                    CouponStatus.USED,
                    LocalDateTime.of(2024, 11, 15, 10, 0),
                    LocalDateTime.of(2024, 11, 30, 23, 59),
                ),
                CouponResponse(
                    4,
                    104,
                    "쿠폰 D",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    15,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2025, 1, 2, 12, 0),
                    LocalDateTime.of(2025, 1, 16, 23, 59),
                ),
                CouponResponse(
                    5,
                    105,
                    "쿠폰 E",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    7000,
                    CouponStatus.EXPIRED,
                    LocalDateTime.of(2025, 1, 3, 14, 0),
                    LocalDateTime.of(2025, 1, 17, 23, 59),
                ),
                CouponResponse(
                    6,
                    106,
                    "쿠폰 F",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    20,
                    CouponStatus.CANCELLED,
                    LocalDateTime.of(2024, 12, 10, 10, 0),
                    LocalDateTime.of(2024, 12, 25, 23, 59),
                ),
                CouponResponse(
                    7,
                    107,
                    "쿠폰 G",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    1000,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2024, 12, 1, 10, 0),
                    LocalDateTime.of(2024, 12, 5, 23, 59),
                ),
                CouponResponse(
                    8,
                    108,
                    "쿠폰 H",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    5,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2025, 1, 5, 15, 0),
                    LocalDateTime.of(2025, 1, 20, 23, 59),
                ),
                CouponResponse(
                    9,
                    109,
                    "쿠폰 I",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    1500,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2024, 11, 25, 10, 0),
                    LocalDateTime.of(2024, 12, 1, 23, 59),
                ),
                CouponResponse(
                    10,
                    110,
                    "쿠폰 J",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.PERCENT,
                    25,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2025, 1, 6, 16, 0),
                    LocalDateTime.of(2025, 1, 21, 23, 59),
                ),
                CouponResponse(
                    11,
                    111,
                    "쿠폰 K",
                    "신규 가입 시 제공되는 감사 쿠폰",
                    CouponDiscountType.AMOUNT,
                    4500,
                    CouponStatus.ACTIVE,
                    LocalDateTime.of(2024, 11, 10, 10, 0),
                    LocalDateTime.of(2024, 11, 20, 23, 59),
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
                                    "issuedAt" -> a.issuedAt.compareTo(b.issuedAt)
                                    "expiresAt" -> a.expiresAt.compareTo(b.expiresAt)
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
    ): ResponseEntity<CustomResponse<Unit>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        return when (request.couponPolicyId) {
            1L -> {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(CustomResponse.success(SuccessCode.COUPON_ISSUE_SUCCESS))
            }

            2L -> {
                throw BusinessException(ErrorCode.COUPON_ISSUE_EXPIRED)
            }

            3L -> {
                throw BusinessException(ErrorCode.COUPON_ALREADY_ISSUED)
            }

            4L -> {
                throw BusinessException(ErrorCode.COUPON_OUT_OF_COUNT)
            }

            else -> {
                throw BusinessException(ErrorCode.COUPON_NOT_FOUND)
            }
        }
    }
}
