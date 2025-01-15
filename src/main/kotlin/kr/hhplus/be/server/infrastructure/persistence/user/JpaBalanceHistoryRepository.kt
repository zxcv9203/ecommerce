package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.BalanceHistory
import kr.hhplus.be.server.domain.user.BalanceHistoryRepository
import org.springframework.stereotype.Repository

@Repository
class JpaBalanceHistoryRepository(
    private val dataJpaBalanceHistoryRepository: DataJpaBalanceHistoryRepository,
) : BalanceHistoryRepository {
    override fun save(balanceHistory: BalanceHistory): BalanceHistory = dataJpaBalanceHistoryRepository.save(balanceHistory)
}
