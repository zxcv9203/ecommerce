CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    balance    BIGINT      NOT NULL COMMENT '잔액',
    version    BIGINT      NOT NULL COMMENT '낙관적락을 위한 버전',
    created_at DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at DATETIME    NOT NULL COMMENT '수정 시간'
);

CREATE TABLE IF NOT EXISTS balance_histories
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL COMMENT '사용자 ID',
    type       VARCHAR(20) NOT NULL COMMENT '잔액 변동 유형',
    amount     BIGINT      NOT NULL COMMENT '잔액 변동 금액',
    created_at DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at DATETIME    NOT NULL COMMENT '수정 시간'
);

CREATE TABLE IF NOT EXISTS products
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL COMMENT '상품 이름',
    description TEXT         NOT NULL COMMENT '상품 설명',
    price       BIGINT       NOT NULL COMMENT '상품 가격',
    stock       INT          NOT NULL COMMENT '상품 재고',
    created_at  DATETIME     NOT NULL COMMENT '생성 시간',
    updated_at  DATETIME     NOT NULL COMMENT '수정 시간'
);

CREATE TABLE IF NOT EXISTS orders
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT      NOT NULL COMMENT '주문자 ID',
    total_price   BIGINT      NOT NULL COMMENT '총 가격',
    payment_price BIGINT      NOT NULL COMMENT '결제 가격',
    status        VARCHAR(20) NOT NULL COMMENT '주문 상태',
    created_at    DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at    DATETIME    NOT NULL COMMENT '수정 시간',
    INDEX idx_order_status_created_at (status, created_at)
);

CREATE TABLE IF NOT EXISTS order_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT   NOT NULL COMMENT '주문 ID',
    product_id BIGINT   NOT NULL COMMENT '상품 ID',
    count      INT      NOT NULL COMMENT '주문 수량',
    created_at DATETIME NOT NULL COMMENT '생성 시간',
    updated_at DATETIME NOT NULL COMMENT '수정 시간',
    INDEX idx_order_items_order_id_product_id (order_id, product_id, count)
);


CREATE TABLE IF NOT EXISTS payments
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT      NOT NULL COMMENT '주문 ID',
    amount     BIGINT      NOT NULL COMMENT '결제 금액',
    type       VARCHAR(20) NOT NULL COMMENT '결제 수단',
    status     VARCHAR(20) NOT NULL COMMENT '결제 상태',
    created_at DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at DATETIME    NOT NULL COMMENT '수정 시간'
);

CREATE TABLE IF NOT EXISTS coupons
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id  BIGINT      NOT NULL COMMENT '쿠폰 정책 ID',
    user_id    BIGINT      NOT NULL COMMENT '사용자 ID',
    order_id   BIGINT DEFAULT NULL COMMENT '주문 ID',
    status     VARCHAR(20) NOT NULL COMMENT '쿠폰 상태',
    version    BIGINT      NOT NULL COMMENT '낙관적락을 위한 버전',
    created_at DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at DATETIME    NOT NULL COMMENT '수정 시간',
    UNIQUE KEY uk_policy_user (policy_id, user_id)
);

CREATE TABLE IF NOT EXISTS coupon_policies
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL COMMENT '쿠폰 정책 이름',
    description     VARCHAR(512) NOT NULL COMMENT '쿠폰 정책 설명',
    total_count     INT          NOT NULL COMMENT '총 쿠폰 수량',
    current_count   INT          NOT NULL COMMENT '현재 쿠폰 수량',
    start_time      DATETIME     NOT NULL COMMENT '시작 시간',
    end_time        DATETIME     NOT NULL COMMENT '종료 시간',
    discount_type   VARCHAR(20)  NOT NULL COMMENT '할인 유형',
    discount_amount BIGINT       NOT NULL COMMENT '할인 금액',
    version         BIGINT       NOT NULL DEFAULT 0 COMMENT '낙관적락을 위한 버전',
    created_at      DATETIME     NOT NULL COMMENT '생성 시간',
    updated_at      DATETIME     NOT NULL COMMENT '수정 시간'
);

CREATE TABLE IF NOT EXISTS outbox
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_id BIGINT      NOT NULL COMMENT '이벤트 ID',
    event_type   VARCHAR(20) NOT NULL COMMENT '이벤트 유형',
    retry_count  INT         NOT NULL COMMENT '재시도 횟수',
    status       VARCHAR(20) NOT NULL COMMENT '전송 상태',
    payload      JSON        NOT NULL COMMENT '전송 데이터',
    created_at   DATETIME    NOT NULL COMMENT '생성 시간',
    updated_at   DATETIME    NOT NULL COMMENT '수정 시간'
)