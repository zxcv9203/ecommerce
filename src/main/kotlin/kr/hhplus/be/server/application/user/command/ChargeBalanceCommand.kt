package kr.hhplus.be.server.application.user.command

data class ChargeBalanceCommand(
    val userId: Long,
    val amount: Long,
)
