package kr.hhplus.be.server.api.coupon

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.api.coupon.request.IssueCouponRequest
import kr.hhplus.be.server.api.coupon.response.CouponsResponse
import kr.hhplus.be.server.common.model.CustomResponse
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

@Tag(name = "유저 쿠폰 API", description = "결제시 할인을 받을 수 있는 쿠폰을 관리하는 API")
interface UserCouponApi {
    @Operation(
        summary = "유저의 쿠폰 목록 조회",
        description = "유저의 쿠폰 목록을 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "내 쿠폰 목록을 조회합니다.",
            ),
            ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다."),
        ],
    )
    fun findAll(
        @Parameter(description = "조회할 유저 ID", required = true)
        userId: Long,
        @ParameterObject
        pageable: Pageable,
    ): ResponseEntity<CustomResponse<CouponsResponse>>

    @Operation(
        summary = "쿠폰 발급",
        description = "쿠폰을 발급합니다.",
        responses = [
            ApiResponse(
                responseCode = "400",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다: \n" +
                        "- 쿠폰 발급 기간이 지난 경우 \n" +
                        "- 쿠폰을 중복 발급받는 경우 \n" +
                        "- 쿠폰 발급 수량이 초과된 경우",
            ),
            ApiResponse(
                responseCode = "404",
                description =
                    "다음과 같은 이유로 요청이 실패했습니다: \n" +
                        "- 존재하지 않는 사용자인 경우 \n" +
                        "- 존재하지 않는 쿠폰인 경우",
            ),
            ApiResponse(
                responseCode = "201",
                description = "쿠폰 발급 성공",
            ),
        ],
    )
    fun issue(
        @Parameter(description = "조회할 유저 ID", required = true)
        userId: Long,
        @RequestBody(description = "쿠폰 발급 요청 정보", required = true)
        request: IssueCouponRequest,
    ): ResponseEntity<CustomResponse<Unit>>
}
