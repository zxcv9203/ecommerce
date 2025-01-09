package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.api.user.response.toBalanceResponse
import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class UserBalanceUseCase(
    private val userService: UserService,
) {
    fun chargeBalance(command: ChargeBalanceCommand) =
        userService
            .chargeBalance(command)

    fun getBalance(userId: Long): UserBalanceResponse =
        userService
            .getById(userId)
            .toBalanceResponse()
}
