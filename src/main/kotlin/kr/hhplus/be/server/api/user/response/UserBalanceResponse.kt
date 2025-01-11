package kr.hhplus.be.server.api.user.response

import kr.hhplus.be.server.domain.user.User

data class UserBalanceResponse(
    val amount: Long,
)

fun User.toBalanceResponse() =
    UserBalanceResponse(
        amount = this.balance,
    )
