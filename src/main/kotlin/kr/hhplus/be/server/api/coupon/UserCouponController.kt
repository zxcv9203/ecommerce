package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.api.coupon.response.CouponResponse
import kr.hhplus.be.server.api.coupon.response.CouponsResponse
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.ApiResponse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/users/{userId}/coupons")
class UserCouponController {
    @GetMapping
    fun findAll(
        @PathVariable userId: Long,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<CouponsResponse>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val coupons =
            listOf(
                CouponResponse(
                    1,
                    101,
                    "AMOUNT",
                    5000,
                    "ACTIVE",
                    LocalDateTime.of(2025, 1, 1, 10, 0),
                    LocalDateTime.of(2025, 1, 15, 23, 59),
                ),
                CouponResponse(
                    2,
                    102,
                    "PERCENT",
                    10,
                    "USED",
                    LocalDateTime.of(2024, 12, 20, 10, 0),
                    LocalDateTime.of(2024, 12, 31, 23, 59),
                ),
                CouponResponse(
                    3,
                    103,
                    "AMOUNT",
                    3000,
                    "EXPIRED",
                    LocalDateTime.of(2024, 11, 15, 10, 0),
                    LocalDateTime.of(2024, 11, 30, 23, 59),
                ),
                CouponResponse(
                    4,
                    104,
                    "PERCENT",
                    15,
                    "ACTIVE",
                    LocalDateTime.of(2025, 1, 2, 12, 0),
                    LocalDateTime.of(2025, 1, 16, 23, 59),
                ),
                CouponResponse(
                    5,
                    105,
                    "AMOUNT",
                    7000,
                    "ACTIVE",
                    LocalDateTime.of(2025, 1, 3, 14, 0),
                    LocalDateTime.of(2025, 1, 17, 23, 59),
                ),
                CouponResponse(
                    6,
                    106,
                    "PERCENT",
                    20,
                    "USED",
                    LocalDateTime.of(2024, 12, 10, 10, 0),
                    LocalDateTime.of(2024, 12, 25, 23, 59),
                ),
                CouponResponse(
                    7,
                    107,
                    "AMOUNT",
                    1000,
                    "EXPIRED",
                    LocalDateTime.of(2024, 12, 1, 10, 0),
                    LocalDateTime.of(2024, 12, 5, 23, 59),
                ),
                CouponResponse(
                    8,
                    108,
                    "PERCENT",
                    5,
                    "ACTIVE",
                    LocalDateTime.of(2025, 1, 5, 15, 0),
                    LocalDateTime.of(2025, 1, 20, 23, 59),
                ),
                CouponResponse(
                    9,
                    109,
                    "AMOUNT",
                    1500,
                    "USED",
                    LocalDateTime.of(2024, 11, 25, 10, 0),
                    LocalDateTime.of(2024, 12, 1, 23, 59),
                ),
                CouponResponse(
                    10,
                    110,
                    "PERCENT",
                    25,
                    "ACTIVE",
                    LocalDateTime.of(2025, 1, 6, 16, 0),
                    LocalDateTime.of(2025, 1, 21, 23, 59),
                ),
                CouponResponse(
                    11,
                    111,
                    "AMOUNT",
                    4500,
                    "EXPIRED",
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
            .body(ApiResponse.success(SuccessCode.COUPON_LIST_QUERY, response))
    }

    @PostMapping
    fun issue(
        @PathVariable userId: Long,
        @RequestBody request: IssueCouponRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        return when (request.couponPolicyId) {
            1L -> {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(SuccessCode.COUPON_ISSUE_SUCCESS))
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
