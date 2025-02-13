package kr.hhplus.be.server.common.constant

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val status: HttpStatus,
    val message: String,
) {
    USER_BALANCE_CHARGE(HttpStatus.OK, "잔액 충전에 성공했습니다."),
    USER_BALANCE_QUERY(HttpStatus.OK, "잔액 조회에 성공했습니다."),

    PRODUCT_QUERY(HttpStatus.OK, "상품 조회에 성공했습니다."),
    POPULAR_PRODUCT_QUERY(HttpStatus.OK, "인기 상품 조회에 성공했습니다."),

    COUPON_ISSUE_SUCCESS(HttpStatus.OK, "쿠폰 발급 요청에 성공했습니다."),
    COUPON_LIST_QUERY(HttpStatus.OK, "내 쿠폰 목록 조회에 성공했습니다."),

    ORDER_CREATED(HttpStatus.CREATED, "주문이 성공적으로 생성되었습니다."),

    PAYMENT_COMPLETED(HttpStatus.OK, "결제가 성공적으로 완료되었습니다."),

    EXTERNAL_PAYMENT_API_COMPLETED(HttpStatus.OK, "외부 데이터 플랫폼에 결제 데이터 전송에 성공했습니다."),
}
