package kr.hhplus.be.server.api.user

import kr.hhplus.be.server.api.user.request.UserBalanceRequest
import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.application.user.UserBalanceUseCase
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/{userId}/balance")
class UserBalanceController(
    private val userBalanceUseCase: UserBalanceUseCase,
) : UserBalanceApi {
    @PatchMapping
    override fun charge(
        @PathVariable userId: Long,
        @RequestBody request: UserBalanceRequest,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>> =
        request
            .toCommand(userId)
            .let { userBalanceUseCase.chargeBalance(it) }
            .let { CustomResponse.success(SuccessCode.USER_BALANCE_CHARGE, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }

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
