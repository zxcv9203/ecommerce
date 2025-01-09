package kr.hhplus.be.server.common.model

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode

data class CustomResponse<T>(
    val code: Int,
    val message: String,
    val data: T,
) {
    companion object {
        fun <T> success(
            code: SuccessCode,
            data: T,
        ): CustomResponse<T> = CustomResponse(code.status.value(), code.message, data)

        fun success(code: SuccessCode): CustomResponse<Unit> = CustomResponse(code.status.value(), code.message, Unit)

        fun fail(code: ErrorCode): CustomResponse<Unit> = CustomResponse(code.status.value(), code.message, Unit)
    }
}
