# 1주차 (설계 주)

## 요구사항 분석 및 설계 (총 6 MD)

### 1. 마일스톤 작성 (0.5 MD)

- 전체 프로젝트 계획 작성
- 주요 단계 및 완료 목표 설정

### 2. 요구사항 분석 (1 MD)

- 유사 서비스 분석
- 요구사항 정의 및 비즈니스 규칙 도출

### 3. 설계 문서 작성 (1.5 MD)

- 시퀀스 다이어그램을 통해 주요 API 및 비즈니스 로직의 흐름 정의
- 기술 스택 작성

### 4. ERD 작성 (1 MD)

- 도메인 모델 분석
- 테이블 및 관계 정의
- 데이터 정규화와 비정규화 검토

### 5. API 명세 작성 (1 MD)

- API 요구사항 정의
- API 입력/출력 스펙 정의

### 6. Mock API 작성 (1 MD)

- Mock 데이터 생성 및 API 엔드포인트 구현

---

# 2주차 (기능 구현 주)

## 주요 기능 구현 (총 6 MD)

### 1. 상품 조회 API (1 MD)

- 상품 정보(ID, 이름, 가격, 잔여수량) 조회
- 조회 시점의 상품별 정확한 잔여수량 반환

### 2. 잔액 충전 / 조회 API (1.5 MD)

- 사용자 식별자를 기반으로 잔액 충전
- 사용자 잔액 조회 기능 구현

### 3. 주문 / 결제 API (2 MD)

- 상품 ID 및 수량을 기반으로 주문 처리
- 잔액 기반 결제 로직 구현
- 결제 성공 시 잔액 차감 및 주문 데이터 플랫폼으로 전송(Mock API 활용)

### 4. 상위 상품 조회 API (0.5 MD)

- 최근 3일간 가장 많이 팔린 상위 5개 상품 조회
- 통계 데이터 처리를 위해 스케줄러 사용

### 5. 선착순 쿠폰 기능 API (1 MD)

- 선착순 쿠폰 발급 기능
- 보유 쿠폰 목록 조회

---

# 3주차 (리팩토링 주)

## 리팩토링 (총 6 MD)

### 1. 구현 코드에 대해 리팩토링 진행 (6 MD)

- 코드 중복 및 가독성 개선 등의 리팩토링 진행
- 성능 최적화 및 확장성 고려한 코드 개선