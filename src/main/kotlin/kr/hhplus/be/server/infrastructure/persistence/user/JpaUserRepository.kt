package kr.hhplus.be.server.infrastructure.persistence.user

import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaUserRepository(
    private val dataJpaUserRepository: DataJpaUserRepository,
) : UserRepository {
    override fun findById(id: Long): User? = dataJpaUserRepository.findByIdOrNull(id)

    override fun save(user: User): User = dataJpaUserRepository.saveAndFlush(user)

    override fun existsById(id: Long): Boolean = dataJpaUserRepository.existsById(id)
}
