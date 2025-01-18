package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.BalanceHistory
import org.springframework.data.jpa.repository.JpaRepository

interface DataJpaBalanceHistoryRepository : JpaRepository<BalanceHistory, Long>
