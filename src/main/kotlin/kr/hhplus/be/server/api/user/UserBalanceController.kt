package kr.hhplus.be.server.api.user

import kr.hhplus.be.server.api.user.request.UserBalanceRequest
import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/{userId}/balance")
class UserBalanceController : UserBalanceApi {
    @PatchMapping
    override fun charge(
        @PathVariable userId: Long,
        @RequestBody request: UserBalanceRequest,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        return when {
            request.amount < 10_000 -> throw BusinessException(ErrorCode.USER_BALANCE_BELOW_MINIMUM)
            request.amount >= 10_000_000 -> throw BusinessException(ErrorCode.USER_BALANCE_EXCEEDS_LIMIT)
            else -> {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(CustomResponse.success(SuccessCode.USER_BALANCE_CHARGE, UserBalanceResponse(request.amount)))
            }
        }
    }

    @GetMapping
    override fun get(
        @PathVariable userId: Long,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>> {
        if (userId != 1L) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomResponse.success(SuccessCode.USER_BALANCE_QUERY, UserBalanceResponse(20000)))
    }
}
