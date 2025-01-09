package kr.hhplus.be.server.domain.user

interface BalanceHistoryRepository {
    fun save(balanceHistory: BalanceHistory): BalanceHistory
}
