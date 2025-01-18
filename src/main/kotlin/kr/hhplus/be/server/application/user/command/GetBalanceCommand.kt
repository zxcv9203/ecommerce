package kr.hhplus.be.server.application.user.command

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException

data class GetBalanceCommand(
    val userId: Long,
) {
    companion object {
        fun of(
            userId: Long,
            authenticationId: Long,
        ): GetBalanceCommand {
            if (userId != authenticationId) throw BusinessException(ErrorCode.FORBIDDEN)
            return GetBalanceCommand(
                userId = userId,
            )
        }
    }
}
