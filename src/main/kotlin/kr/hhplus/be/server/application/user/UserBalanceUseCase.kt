package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.application.user.command.GetBalanceCommand
import kr.hhplus.be.server.application.user.info.UserBalanceInfo
import kr.hhplus.be.server.application.user.info.toBalanceResult
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class UserBalanceUseCase(
    private val userService: UserService,
) {
    fun chargeBalance(command: ChargeBalanceCommand) =
        userService
            .chargeBalance(command)

    fun getBalance(command: GetBalanceCommand): UserBalanceInfo =
        userService
            .getById(command.userId)
            .toBalanceResult()
}
