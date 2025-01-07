package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun getById(id: Long): User =
        userRepository.findById(id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
}
