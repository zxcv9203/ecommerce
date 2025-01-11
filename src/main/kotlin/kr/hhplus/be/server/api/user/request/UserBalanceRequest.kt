package kr.hhplus.be.server.api.user.request

import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException

data class UserBalanceRequest(
    val amount: Long?,
) {
    init {
        if (amount == null) {
            throw BusinessException(ErrorCode.USER_BALANCE_BELOW_MINIMUM)
        }
    }

    fun toCommand(userId: Long) =
        ChargeBalanceCommand(
            userId = userId,
            amount = amount!!,
        )
}
