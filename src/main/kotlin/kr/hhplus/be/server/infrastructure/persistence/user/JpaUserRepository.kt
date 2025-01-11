package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long>
