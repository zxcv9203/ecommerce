package kr.hhplus.be.server.common.constant

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_BALANCE_BELOW_MINIMUM(HttpStatus.BAD_REQUEST, "충전 금액은 최소 10,000원 이상이어야 합니다."),
    USER_BALANCE_EXCEEDS_LIMIT(HttpStatus.BAD_REQUEST, "잔액 상한을 초과할 수 없습니다."),
    USER_BALANCE_CHARGE_FAILED(HttpStatus.BAD_REQUEST, "잔액 충전에 실패했습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    COUPON_ISSUE_NOT_STARTED(HttpStatus.BAD_REQUEST, "쿠폰 발급 기간이 아직 시작되지 않았습니다."),
    COUPON_ISSUE_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰 발급 기간이 지났습니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "이미 발급된 쿠폰이 있습니다."),
    COUPON_OUT_OF_COUNT(HttpStatus.BAD_REQUEST, "쿠폰 발급 수량이 초과되었습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "상품의 재고가 부족합니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰이 만료되었습니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    ORDER_AMOUNT_INVALID(HttpStatus.BAD_REQUEST, "주문 금액은 0원 이하일 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    ORDER_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 주문입니다."),
    ORDER_INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 주문 상태입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
}
