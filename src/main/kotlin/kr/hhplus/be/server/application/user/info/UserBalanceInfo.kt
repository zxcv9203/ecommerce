package kr.hhplus.be.server.application.user.info

import kr.hhplus.be.server.domain.user.User

data class UserBalanceInfo(
    val amount: Long,
)

fun User.toBalanceResult() =
    UserBalanceInfo(
        amount = this.balance,
    )
