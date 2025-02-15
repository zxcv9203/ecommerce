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


-- 테스트용 더미데이터
# -- 1. 트랜잭션 시작
# SET autocommit = 0;
# START TRANSACTION;
#
# -- 2. users 테이블 (100만 개) 생성
# INSERT INTO users (name, balance, version, created_at, updated_at)
# SELECT
#     CONCAT('User', t.n),
#     FLOOR(RAND() * 1000000),
#     0,
#     NOW(), NOW()
# FROM
#     (SELECT @row := @row + 1 AS n FROM
#         (SELECT 1 FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) a
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) b
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) c
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) d
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) e
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) f
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) g
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) h
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) i
#                            CROSS JOIN (SELECT @row := 0) j) t
#      LIMIT 500000) t;
#
# -- 3. products 테이블 (100만 개)
# INSERT INTO products (name, description, price, stock, created_at, updated_at)
# SELECT
#     CONCAT('Product', t.n),
#     'Sample product description',
#     FLOOR(RAND() * 100000),
#     FLOOR(RAND() * 100),
#     NOW(), NOW()
# FROM
#     (SELECT @row := @row + 1 AS n FROM
#         (SELECT 1 FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) a
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) b
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) c
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) d
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) e
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) f
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) g
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) h
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) i
#                            CROSS JOIN (SELECT @row := 0) j) t
#      LIMIT 500000) t;
#
# -- 4. balance_histories 테이블 (100만 개)
# INSERT INTO balance_histories (user_id, type, amount, created_at, updated_at)
# SELECT
#     u.id,
#     IF(RAND() > 0.5, 'CHARGE', 'USE'),
#     FLOOR(RAND() * 100000),
#     NOW(), NOW()
# FROM users u
# LIMIT 500000;
#
# -- 5. orders 테이블 (100만 개)
# INSERT INTO orders (user_id, total_price, payment_price, status, created_at, updated_at)
# SELECT
#     u.id,
#     FLOOR(RAND() * 50000) + 10000,
#     FLOOR(RAND() * 50000) + 10000,
#     IF(RAND() > 0.5, 'CONFIRMED', 'PENDING'),
#     NOW() - INTERVAL FLOOR(RAND() * 7) DAY,  -- 현재 시간에서 0~7일 전까지 랜덤
#     NOW()
# FROM users u
# LIMIT 500000;
#
#
# -- 6. order_items 테이블 (100만 개)
# INSERT INTO order_items (order_id, product_id, count, created_at, updated_at)
# SELECT
#     o.id,
#     p.id,
#     FLOOR(RAND() * 10) + 1,
#     NOW(), NOW()
# FROM orders o
#          JOIN products p ON p.id = o.id
# LIMIT 500000;
#
# -- 7. payments 테이블 (100만 개, type = 'BALANCE' 고정)
# INSERT INTO payments (order_id, amount, type, status, created_at, updated_at)
# SELECT
#     o.id,
#     FLOOR(RAND() * 50000) + 10000,
#     'BALANCE',
#     'SUCCESS',
#     NOW(), NOW()
# FROM orders o
# LIMIT 500000;
#
# -- 3. coupon_policies 테이블 (100만 개, discount_type = 'PERCENT' 고정)
# INSERT INTO coupon_policies (name, description, total_count, current_count, start_time, end_time, discount_type, discount_amount, version, created_at, updated_at)
# SELECT
#     CONCAT('Policy', t.n),
#     'Sample coupon policy',
#     FLOOR(RAND() * 1000) + 500,
#     FLOOR(RAND() * 500),
#     NOW(),
#     DATE_ADD(NOW(), INTERVAL 30 DAY),
#     'PERCENT',
#     FLOOR(RAND() * 50) + 5,
#     0,
#     NOW(), NOW()
# FROM
#     (SELECT @row := @row + 1 AS n FROM
#         (SELECT 1 FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) a
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) b
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) c
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) d
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) e
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) f
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) g
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) h
#                            CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) i
#                            CROSS JOIN (SELECT @row := 0) j) t
#      LIMIT 500000) t;
#
# -- 4. coupons 테이블 (100만 개, 모든 쿠폰을 user_id = 1에게 발급)
# INSERT INTO coupons (policy_id, user_id, order_id, status, version, created_at, updated_at)
# SELECT
#     cp.id,  -- 모든 쿠폰 정책(policy_id)에 대해
#     1,      -- user_id = 1에게만 발급
#     NULL,
#     'ACTIVE',
#     0,
#     NOW(), NOW()
# FROM coupon_policies cp
# LIMIT 500000;
# -- 10. 트랜잭션 커밋
# COMMIT;
# SET autocommit = 1;
