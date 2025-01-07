package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository :
    UserRepository,
    JpaRepository<User, Long>
