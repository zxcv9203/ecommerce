package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {
    override fun findById(id: Long): User? = jpaUserRepository.findByIdOrNull(id)

    override fun save(user: User): User = jpaUserRepository.saveAndFlush(user)
}
