package kr.hhplus.be.server.api.user

import kr.hhplus.be.server.api.user.request.UserBalanceRequest
import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.api.user.response.toResponse
import kr.hhplus.be.server.application.user.UserBalanceUseCase
import kr.hhplus.be.server.application.user.command.GetBalanceCommand
import kr.hhplus.be.server.common.constant.AuthConstants
import kr.hhplus.be.server.common.constant.SuccessCode
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
        @RequestAttribute(AuthConstants.AUTH_ID) authenticationId: Long,
        @RequestBody request: UserBalanceRequest,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>> =
        request
            .toCommand(userId, authenticationId)
            .let { userBalanceUseCase.chargeBalance(it) }
            .toResponse()
            .let { CustomResponse.success(SuccessCode.USER_BALANCE_CHARGE, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }

    @GetMapping
    override fun get(
        @PathVariable userId: Long,
        @RequestAttribute(AuthConstants.AUTH_ID) authenticationId: Long,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>> =
        GetBalanceCommand
            .of(userId, authenticationId)
            .let { userBalanceUseCase.getBalance(it) }
            .toResponse()
            .let { CustomResponse.success(SuccessCode.USER_BALANCE_QUERY, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
