package kr.hhplus.be.server.common.model

import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.constant.SuccessCode

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T,
) {
    companion object {
        fun <T> success(
            code: SuccessCode,
            data: T,
        ): ApiResponse<T> = ApiResponse(code.status.value(), code.message, data)

        fun success(code: SuccessCode): ApiResponse<Unit> = ApiResponse(code.status.value(), code.message, Unit)

        fun fail(code: ErrorCode): ApiResponse<Unit> = ApiResponse(code.status.value(), code.message, Unit)
    }
}
