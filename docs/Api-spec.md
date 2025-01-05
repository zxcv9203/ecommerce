# API 스펙

## 1. 잔액 충전 API

### 요구사항

- 최소 충전 금액은 10,000원 입니다.
- 가질수 있는 최대 금액은 9,999,999원 입니다.

### 요청

- `PATCH /api/v1/users/{userId}/balance`

```json
{
  "amount": 10000
}
```

#### 참고

Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.

### 응답

#### 성공 (예시)

```json
{
  "code": 200,
  "message": "잔액 충전에 성공했습니다.",
  "data": {
    "amount": 10000
  }
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

#### 실패 (충전 금액이 최소 금액 미만인 경우)

```json
{
  "code": 400,
  "message": "충전 금액은 최소 10,000원 이상이어야 합니다.",
  "data": {}
}
```

#### 실패 (충전 후 잔액 상한 초과)

```json
{
  "code": 400,
  "message": "잔액 상한을 초과할 수 없습니다.",
  "data": {}
}
```

## 2. 잔액 조회 API

### 요청

- `GET /api/v1/users/{userId}/balance`

#### 참고

Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.

### 응답

#### 성공 (예시)

```json
{
  "code": 200,
  "message": "잔액 조회에 성공했습니다.",
  "data": {
    "amount": 10000
  }
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

## 3. 상품 조회 API

### 요구사항

### 요청

- `GET /api/v1/products`
    - `page` : 페이지 번호 (기본값: 0)
    - `size` : 페이지 크기 (기본값: 20)
    - `sort` : 정렬 방식 (예: price,desc)

#### 예시

- `GET /api/v1/products?page=0&size=10&sort=price,desc`

### 응답

#### 성공 (예시)

```json
{
  "code": 200,
  "message": "상품 목록 조회에 성공했습니다.",
  "data": {
    "products": [
      {
        "id": 1,
        "name": "상품 A",
        "price": 12000,
        "stock": 50
      }
    ],
    "hasNext": false
  }
}
```

## 4. 선착순 쿠폰 발급 API

### 요청

- `POST /api/v1/users/{userId}/coupons`

```json
{
  "couponPolicyId": 1
}
```

#### 참고

Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.
Mock API에서는 couponPolicyId가 1인 요청만 정상적으로 요청이 가능합니다.
Mock API에서는 couponPolicyId가 2인 요청은 발급기간이 지난 쿠폰입니다.
Mock API에서는 couponPolicyId가 3인 요청은 이미 발급된 쿠폰입니다.
Mock API에서는 couponPolicyId가 4인 요청은 발급 가능 개수를 초과한 쿠폰입니다.
### 응답

#### 성공

```json
{
  "code": 201,
  "message": "쿠폰 발급에 성공했습니다.",
  "data": {}
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

#### 실패 (쿠폰 정책이 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 쿠폰입니다.",
  "data": {}
}
```

#### 실패 (쿠폰 발급 가능 기간이 지난 경우)

```json
{
  "code": 400,
  "message": "쿠폰 발급 기간이 지났습니다.",
  "data": {}
}
```

#### 실패 (이미 해당 쿠폰을 발급받은 경우)

```json
{
  "code": 400,
  "message": "이미 발급된 쿠폰이 있습니다.",
  "data": {}
}
```

#### 실패 (쿠폰 발급 가능 개수 초과)

```json
{
  "code": 400,
  "message": "쿠폰 발급 수량이 초과되었습니다.",
  "data": {}
}
```

## 5. 내 쿠폰 목록 조회 API

### 요청

- `GET /api/v1/users/{userId}/coupons`
    - page: 페이지 번호 (기본값: 0)
    - size: 페이지 크기 (기본값: 20)
    - sort: 정렬 기준 및 방향 (예: issuedAt,desc).

#### 예시

- `GET /api/v1/users/1/coupons?page=0&size=10&sort=issuedAt,desc`

#### 참고

Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.

### 응답

#### 성공 (예시)

```json
{
  "code": 200,
  "message": "내 쿠폰 목록 조회에 성공했습니다.",
  "data": {
    "coupons": [
      {
        "id": 1,
        "policyId": 1,
        "name": "쿠폰 A",
        "description": "쿠폰 A 입니다.",
        "discountType": "FIXED",
        "discountValue": 5000,
        "status": "ACTIVE",
        "issuedAt": "2025-01-01T10:00:00Z",
        "expiresAt": "2025-01-15T23:59:59Z"
      },
      {
        "id": 2,
        "policyId": 2,
        "name": "쿠폰 B",
        "description": "쿠폰 B 입니다.",
        "discountType": "PERCENT",
        "discountValue": 10,
        "status": "USED",
        "issuedAt": "2024-12-20T10:00:00Z",
        "expiresAt": "2024-12-31T23:59:59Z"
      }
    ],
    "hasNext": true
  }
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

## 6. 주문 API

- `POST /api/v1/orders`

### 요청

```json
{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ],
  "couponId": 1
}
```

#### 참고

- Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.
- Mock API에서는 productId가 1, 2인 상품만 정상적으로 요청이 가능합니다.
- Mock API에서 productId가 3인 상품은 항상 재고가 없습니다.
- Mock API에서는 couponId가 1인 쿠폰만 정상적으로 요청이 가능합니다.

### 응답

#### 성공 (예시)

```json
{
  "code": 201,
  "message": "주문이 성공적으로 생성되었습니다.",
  "data": {
    "orderId": 1,
    "totalPrice": 23000
  }
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

#### 실패 (상품이 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 상품입니다.",
  "data": {}
}
```

#### 실패 (쿠폰을 전달받았을 때 전달받은 쿠폰 ID가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 쿠폰입니다.",
  "data": {}
}
```

#### 실패 (쿠폰을 전달받았을 때 쿠폰이 만료된 경우)

```json
{
  "code": 400,
  "message": "쿠폰이 만료되었습니다.",
  "data": {}
}
```

#### 실패 (쿠폰을 전달받았을 때 쿠폰이 사용된 경우)

```json
{
  "code": 400,
  "message": "이미 사용된 쿠폰입니다.",
  "data": {}
}
```

#### 실패 (주문할 상품의 재고가 부족한 경우)

```json
{
  "code": 400,
  "message": "상품 재고가 부족합니다.",
  "data": {}
}
```

#### 실패 (주문 금액이 0원 이하인 경우)

```json
{
  "code": 400,
  "message": "주문 금액은 0원 이하일 수 없습니다.",
  "data": {}
}
```

## 7. 결제 API

### 요청

- `POST /api/v1/payments`

```json
{
  "userId": 1,
  "orderId": 1
}
```

#### 참고

- Mock API에서는 userId가 1인 사용자만 정상적으로 요청이 가능합니다.
- Mock API에서는 orderId가 1인 주문만 정상적으로 요청이 가능합니다.
- Mock API에서 orderId가 2인 주문은 이미 결제가 완료된 상태입니다.
- Mock API에서 orderId가 3인 주문은 상품 재고가 부족합니다.

### 응답

#### 성공

```json
{
  "code": 200,
  "message": "결제가 성공적으로 완료되었습니다.",
  "data": {}
}
```

#### 실패 (사용자가 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 사용자입니다.",
  "data": {}
}
```

#### 실패 (주문이 존재하지 않는 경우)

```json
{
  "code": 404,
  "message": "존재하지 않는 주문입니다.",
  "data": {}
}
```

#### 실패 (주문 상태가 결제 대기 상태가 아닌 경우)

```json
{
  "code": 400,
  "message": "이미 처리된 주문입니다.",
  "data": {}
}
```

#### 실패 (재고 수량이 부족한 경우)

```json
{
  "code": 400,
  "message": "상품 재고가 부족합니다.",
  "data": {}
}
```

#### 실패 (잔액이 부족한 경우)

```json
{
  "code": 400,
  "message": "잔액이 부족합니다.",
  "data": {}
}
```

## 8. 상위 상품 조회 API

### 요청

- `GET /api/v1/popular-products`

### 응답

#### 성공 (예시)

```json
{
  "code": 200,
  "message": "인기 상품 목록 조회에 성공했습니다.",
  "data": {
    "products": [
      {
        "id": 1,
        "rank": 1,
        "name": "상품 A",
        "price": 12000,
        "totalSales": 150
      },
      {
        "id": 2,
        "rank": 2,
        "name": "상품 B",
        "price": 8000,
        "totalSales": 120
      },
      {
        "id": 3,
        "rank": 3,
        "name": "상품 C",
        "price": 5000,
        "totalSales": 100
      },
      {
        "id": 4,
        "rank": 4,
        "name": "상품 D",
        "price": 15000,
        "totalSales": 80
      },
      {
        "id": 5,
        "rank": 5,
        "name": "상품 E",
        "price": 7000,
        "totalSales": 60
      }
    ]
  }
}
```