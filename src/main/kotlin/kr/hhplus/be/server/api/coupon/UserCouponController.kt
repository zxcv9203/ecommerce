package kr.hhplus.be.server.api.coupon

import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.api.coupon.response.CouponsResponse
import kr.hhplus.be.server.api.coupon.response.toResponse
import kr.hhplus.be.server.application.coupon.CouponUseCase
import kr.hhplus.be.server.application.coupon.command.FindUserCouponCommand
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.model.CustomResponse
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
    ): ResponseEntity<CustomResponse<CouponsResponse>> =
        FindUserCouponCommand(userId, pageable)
            .let { couponUseCase.findAllByUserId(it) }
            .toResponse()
            .let { CustomResponse.success(SuccessCode.COUPON_LIST_QUERY, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }

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
