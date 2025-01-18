package kr.hhplus.be.server.api.user.response

import kr.hhplus.be.server.application.user.info.UserBalanceInfo

data class UserBalanceResponse(
    val amount: Long,
)

fun UserBalanceInfo.toResponse() =
    UserBalanceResponse(
        amount = this.amount,
    )
