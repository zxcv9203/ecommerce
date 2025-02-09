TRUNCATE TABLE balance_histories;
TRUNCATE TABLE payments;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE coupons;
TRUNCATE TABLE coupon_policies;
TRUNCATE TABLE products;
TRUNCATE TABLE users;

-- 유저 데이터 생성
INSERT INTO users (id, name, balance, version, created_at, updated_at)
VALUES (1, '유저 A', 0, 0, NOW(), NOW()),
       (2, '유저 B', 0, 0, NOW(), NOW()),
       (3, '유저 C', 0, 0, NOW(), NOW());

-- 쿠폰 정책 데이터 생성
INSERT INTO coupon_policies (id, name, description, total_count, current_count, start_time, end_time,
                             discount_type, discount_amount, created_at, updated_at)
VALUES
    -- 발급 가능한 퍼센트 할인 정책
    (1, '10% 할인', '10프로 주문금액 할인권', 1000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY),
     'PERCENT', 10, NOW(), NOW()),

    -- 발급 가능한 고정 금액 할인 정책
    (2, '5000원 할인', '주문금액 5000원 할인권', 500, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY),
     'AMOUNT', 5000, NOW(), NOW()),

    -- 이미 종료된 정책
    (3, '출시기념 30프로 할인', '서비스 출시기념 30프로 할인 쿠폰', 200, 0, DATE_SUB(NOW(), INTERVAL 60 DAY),
     DATE_SUB(NOW(), INTERVAL 30 DAY),
     'PERCENT', 30, NOW(), NOW());


-- 상품 데이터 생성
INSERT INTO products (id, name, description, price, stock, created_at, updated_at)
VALUES (1, '볼펜', '부드럽게 써지는 검정색 볼펜', 500, 100, NOW(), NOW()),       -- 재고 100개
       (2, '노트', 'A5 크기의 무지 노트', 2000, 50, NOW(), NOW()),          -- 재고 50개
       (3, '파일 폴더', '문서 정리를 위한 투명 파일 폴더', 1500, 30, NOW(), NOW()), -- 재고 30개
       (4, '캔 음료', '시원한 탄산음료', 1200, 0, NOW(), NOW()),             -- 재고 없음
       (5, '마스킹 테이프', '디자인이 예쁜 마스킹 테이프', 1000, 20, NOW(), NOW()); -- 재고 20개

-- 인기상품 조회를 위한 더미 주문 데이터 생성 (테스트 용이라 가격이 맞지 않을 수 있음)
INSERT INTO orders (user_id, total_price, payment_price, status, created_at, updated_at)
SELECT
    FLOOR(RAND() * 3) + 1,  -- 1~3 사이의 랜덤한 user_id
    10000 + FLOOR(RAND() * 90000),  -- 10000~100000 랜덤 총 가격
    10000 + FLOOR(RAND() * 90000),
    'CONFIRMED',  -- 결제 완료된 주문
    NOW(),
    NOW()
FROM
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS a,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS b,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS c,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS d,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS e,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS f,
    (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS g;

INSERT INTO order_items (order_id, product_id, count, created_at, updated_at)
SELECT
    o.id,
    FLOOR(RAND() * 5) + 1,
    FLOOR(RAND() * 10) + 1,
    NOW(),
    NOW()
FROM orders o
         JOIN (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) AS t ON 1=1
LIMIT 200000;
