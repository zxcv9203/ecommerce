package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.application.user.command.ChargeBalanceCommand
import kr.hhplus.be.server.application.user.command.UseBalanceCommand
import kr.hhplus.be.server.application.user.info.UserBalanceInfo
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.orm.ObjectOptimisticLockingFailureException
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
    fun chargeBalance(command: ChargeBalanceCommand): UserBalanceInfo {
        val chargeBalanceUser =
            getById(command.userId)
                .apply { this.charge(command.amount) }

        val savedUser =
            try {
                userRepository.save(chargeBalanceUser)
            } catch (e: ObjectOptimisticLockingFailureException) {
                throw BusinessException(ErrorCode.USER_BALANCE_CHARGE_FAILED)
            }

        BalanceHistory
            .createByCharge(savedUser, command.amount)
            .also { balanceHistoryRepository.save(it) }

        return UserBalanceInfo(savedUser.balance)
    }

    @Transactional
    fun useBalance(command: UseBalanceCommand) {
        val useBalanceUser =
            getById(command.userId)
                .apply { this.use(command.amount) }

        val savedUser =
            try {
                userRepository.save(useBalanceUser)
            } catch (e: OptimisticLockingFailureException) {
                throw BusinessException(ErrorCode.USER_BALANCE_USE_FAILED)
            }

        BalanceHistory
            .createByUse(savedUser, command.amount)
            .also { balanceHistoryRepository.save(it) }
    }
}
