package kr.hhplus.be.server.application.user.command

import kr.hhplus.be.server.domain.user.User

data class UseBalanceCommand(
    val userId: Long,
    val amount: Long,
)

fun User.toUseBalanceCommand(amount: Long) = UseBalanceCommand(id, amount)
