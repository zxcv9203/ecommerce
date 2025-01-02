package kr.hhplus.be.server.common.exception

import kr.hhplus.be.server.common.constant.ErrorCode

class BusinessException(
    val code: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(
        code.message,
        cause,
    )
