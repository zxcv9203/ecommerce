package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class UserBalanceUseCase(
    private val userService: UserService
) {
    fun chargeBalance(command: ChargeBalanceCommand) =
        userService
            .chargeBalance(command)
}