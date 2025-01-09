package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val balanceHistoryRepository: BalanceHistoryRepository,
) {
    fun getById(id: Long): User =
        userRepository.findById(id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

    @Transactional
    fun chargeBalance(command: ChargeBalanceCommand): UserBalanceResponse {
        val chargeBalanceUser =
            getById(command.userId)
                .apply { this.charge(command.amount) }

        val savedUser =
            try {
                userRepository.save(chargeBalanceUser)
            } catch (e: OptimisticLockingFailureException) {
                throw BusinessException(ErrorCode.USER_BALANCE_CHARGE_FAILED)
            }

        BalanceHistory
            .createByCharge(savedUser, command.amount)
            .also { balanceHistoryRepository.save(it) }

        return UserBalanceResponse(savedUser.balance)
    }
}
