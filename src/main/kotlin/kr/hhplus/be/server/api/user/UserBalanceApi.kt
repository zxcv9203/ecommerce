package kr.hhplus.be.server.api.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.api.user.request.UserBalanceRequest
import kr.hhplus.be.server.api.user.response.UserBalanceResponse
import kr.hhplus.be.server.common.model.CustomResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "유저 잔액 API", description = "유저의 잔액을 관리하는 API")
interface UserBalanceApi {
    @Operation(
        summary = "잔액 충전",
        description = "유저의 잔액 충전을 진행합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "잔액 충전에 성공했습니다.",
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 사용자입니다.",
            ),
            ApiResponse(
                responseCode = "400",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다:\n" +
                        "- 충전 금액은 최소 10,000원 이상이어야 합니다.\n" +
                        "- 잔액 상한을 초과할 수 없습니다.",
            ),
        ],
    )
    fun charge(
        @Parameter(description = "충전할 유저 ID", required = true)
        @PathVariable
        userId: Long,
        @Parameter(description = "인증된 유저 ID", required = true)
        authenticationId: Long,
        @RequestBody(description = "잔액 충전 요청 정보", required = true)
        request: UserBalanceRequest,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>>

    @Operation(
        summary = "잔액 조회",
        description = "유저의 현재 잔액을 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "잔액 조회에 성공했습니다.",
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 사용자입니다.",
            ),
        ],
    )
    fun get(
        @Parameter(description = "조회할 유저 ID", required = true)
        @PathVariable
        userId: Long,
        @Parameter(description = "인증된 유저 ID", required = true)
        authenticationId: Long,
    ): ResponseEntity<CustomResponse<UserBalanceResponse>>
}
