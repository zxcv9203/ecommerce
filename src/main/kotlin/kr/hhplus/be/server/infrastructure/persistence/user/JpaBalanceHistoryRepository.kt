package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.BalanceHistory
import kr.hhplus.be.server.domain.user.BalanceHistoryRepository
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBalanceHistoryRepository :
    BalanceHistoryRepository,
    JpaRepository<BalanceHistory, Long>
