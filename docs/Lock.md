# 락 제어 방식

## 1. 비관적 락

- 비관적 락은 데이터를 읽거나 수정할 때, 다른 트랜잭션이 해당 데이터에 접근하지 못하도록 미리 락(Lock)을 걸어 데이터의 충돌 가능성을 방지하는 방식입니다.
- SELECT ... FOR UPDATE (X-LOCK), SELECT ... LOCK IN SHARE MODE (S-LOCK) 등의 SQL문을 사용하여 락을 걸 수 있습니다.

### 특징

- 데이터 충돌 방지
    - 데이터를 수정하는 동안 다른 트랜잭션의 접근을 차단하여 충돌을 사전에 예방할 수 있습니다.
- 트랜잭션 기반 락
    - 락은 트랜잭션 범위 내에서만 유지되며, 트랜잭션이 종료되면, 자동으로 해제됩니다.
- 성능 저하 우려
    - 락으로 인해 경합이 발생하면 대기 시간이 길어질 수 있어 성능 문제가 발생할 수 있습니다.

### 장점

1. 데이터 무결성 보장

- 충돌 가능성이 높은 상황에서도 데이터 일관성을 보장합니다.

2. 간단한 사용법

- 데이터베이스 수준에서 락을 관리하므로 구현이 간단합니다.

### 단점

1. 성능 저하

- 다른 트랜잭션이 락이 해제될 때까지 대기해야 하므로 성능이 저하될 수 있습니다.

2. 데드락 발생 가능성

- 여러 트랜잭션이 서로 다른 자원에 락을 걸고 대기하는 상황에서 데드락이 발생할 가능성이 있습니다.

### 사용 방법

- id = 1인 쿠폰 정책을 비관적락으로 조회하는 쿼리입니다. id = 1인 쿠폰 정책은 해당 트랜잭션이 종료될 때까지 다른 트랜잭션에서 수정할 수 없습니다.

```mysql
start transaction;
SELECT *
FROM coupon_policies
WHERE id = 1 FOR
UPDATE;
...
...
```

### JPA에서 비관적락 사용하기

- JPA에서 비관적락을 사용하려면 @Lock 어노테이션을 사용하면 됩니다.

```kotlin
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
fun findByIdWithLock(id: Long): CouponPolicy?
```

## 2. 낙관적 락

- 낙관적 락은 데이터 충돌 가능성이 적다고 가정하고, 데이터를 수정하기 전에 데이터의 버전(또는 타임스탬프)을 확인하여 충돌을 감지하는 방식입니다.
- 충돌이 발생했을 경우 롤백하거나 재시도를 통해 데이터를 안전하게 처리합니다.

### 특징

- 버전 기반 충돌 감지
    - 데이터를 수정할 때, 이전에 읽었던 버전과 현재 버전을 비교하여 충돌 여부를 확인합니다.
- 충돌 발생 시 롤백
    - 버전이 다를 경우 예외를 발생시키거나 트랜잭션을 롤백합니다.
- 락 없이 병렬 처리 가능
    - 실제로 락을 걸지 않으므로 트랜잭션 간 경합 없이 동작합니다.

### 장점

- 병렬 처리에 유리
    - 실제로 락을 걸지 않으므로 트랜잭션 간 경합 없이 동작합니다.
- 데이터 충돌 방지
    - 데이터를 수정하기 전에 충돌을 감지하여 데이터 무결성을 보장합니다.

### 단점

- 충돌 시 추가 처리 필요
    - 충돌이 발생하면 예외 처리를 통해 재시도 또는 다른 로직을 구현해야 합니다.
- 높은 충돌 발생률 시 성능 저하
    - 충돌이 자주 발생하는 경우 재시도로 인해 성능이 저하될 수 있습니다.

### 쿼리 예시

### JPA에서 낙관적락 사용하기

- JPA에서 낙관적 락을 사용하려면 @Version 어노테이션을 사용하여 버전을 관리합니다.

```kotlin
@Entity
class User(
    ....
    @Version
@Column(name = "version", nullable = false)
@Comment("낙관적락을 위한 버전")
val version: Long = 0,
....
)
```

- JPA에서 충돌 발생시 OptimisticLockException 예외가 발생합니다.
    - Spring Data JPA를 사용하는 경우 스프링의 데이터 액세스 예외 계층에서 OptimisticLockingException을
      ObjectOptimisticLockingFailureException으로 변환합니다.
- JPA의 낙관적락은 엔티티를 저장하거나 플러시할 때 데이터베이스와 버전을 비교합니다.
    - 이 때문에 트랜잭션 종료시점이거나 플러시 시점 전에는 충돌 여부를 알 수 없기 때문에 명시적으로 flush를 호출하여 충돌이 발생했는지 여부를 확인할 수 있습니다.

## 3. NamedLock

- Named Lock은 이름을 기반으로 락을 걸어 동시성 제어를 수행하는 방식입니다.
- 주로 데이터베이스에서 제공하는 GET_LOCK과 같은 함수나, 락 관리 라이브러리를 사용하여 구현됩니다.

### 특징

- 이름을 기반으로 제어
    - 락의 이름을 기반으로 특정 자원에 대해 락을 걸거나 해제합니다.
    - 같은 이름의 락을 사용하면 동일한 자원을 보호할 수 있습니다.
- 트랜잭션과 독립적
    - Named Lock은 일반적으로 데이터베이스 트랜잭션과 별개로 동작합니다.
    - 트랜잭션 종료와 상관없이 명시적으로 락을 해제해야 합니다.

### 장점

- 트랜잭션 외부에서도 사용 가능
    - 특정 자원에 대한 락을 트랜잭션과 독립적으로 관리할 수 있습니다.
- 간단한 구현
    - 락 이름만으로 특정 자원을 보호할 수 있어 간단합니다.

### 단점

- 락 해제 누락 가능성
    - 락 해제를 명시적으로 수행하지 않으면 자원이 잠긴 상태로 유지될 수 있습니다.
- 분산 환경에서의 제약
    - 데이터베이스를 사용하므로 분산 환경에서는 성능 저하 또는 확장성 문제가 발생할 수 있습니다.

### 사용 예시

```sql
-- 락 획득(10초 후 타임아웃 되는 coupon_1이라는 이름의 락)
SELECT GET_LOCK('coupon_1', 10);

-- 작업 수행
UPDATE coupon_policies
SET name = 'Updated Name'
WHERE id = 1;

-- 락 해제
SELECT RELEASE_LOCK('coupon_1');
```

### JPA에서 Named Lock 사용하기

- JPA에서 Named Lock을 사용하려면 Native Query를 사용하여 GET_LOCK, RELEASE_LOCK 함수를 호출합니다.

```kotlin
@Query(value = "SELECT GET_LOCK(:lockName, :timeout)", nativeQuery = true)
fun getLock(@Param("lockName") lockName: String, @Param("timeout") timeout: Int): Boolean

@Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
fun releaseLock(@Param("lockName") lockName: String): Boolean
```

## 4. 분산락 (Redis)

- 분산 락은 여러 서버에서 공유 자원에 대한 접근을 동기화하기 위해 사용됩니다.
- Redis와 같은 분산 캐시 시스템에서 제공하는 기능을 활용하여 구현할 수 있습니다.

### 특징

- 분산 환경 지원
    - 여러 노드에서 동시에 자원 접근을 제어할 수 있습니다.
- TTL 설정 가능
    - 락의 유효시간을 설정하여 무한정 락이 유지되는 것을 방지합니다.
- 경량화
    - Redis와 같은 인메모리 데이터베이스를 사용하므로 빠른 처리 속도를 제공합니다.

### 사용 예시

#### 1. 심플락

- 심플락은 분산 환경에서 기본적으로 사용하는 락 방식으로, 자원에 대해 락을 걸 때 성공 여부를 바로 반환합니다.
- SETNX 명령어를 활용해 락이 이미 존재하면 실패하고, 그렇지 않으면 락을 설정합니다.

- 장점
    - 간단하고 빠르게 구현 가능합니다.
    - TTL로 인해 락이 영구적으로 유지되는 것을 방지합니다.
- 단점
    - 락 획득 실패 시 반복적인 요청이 필요한 경우 CPU 자원이 낭비될 수 있습니다.
    - 네트워크 장애로 인해 락이 정상 해제되지 않을 위험성이 있습니다.

```kotlin
@Service
class SimpleLockService(private val redisTemplate: RedisTemplate<String, String>) {

    fun lock(key: String, ttl: Long): Boolean {
        val success = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", Duration.ofSeconds(ttl))
        return success ?: false
    }

    fun unlock(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}
```

#### 2. 스핀락

- 스핀락은 락을 획득하려고 시도했을 때 실패하면 반복적으로 재시도하여 락을 획득하는 방식입니다. 락 획득 실패 시 일정 시간 대기 후 다시 시도하여 락을 획득하려고 합니다.
- 장점
    - 락이 해제되기를 기다릴 수 있으므로 특정 자원에 대한 락 획득 가능성이 높습니다.
    - 네트워크 지연이나 락 획득 실패 상황에서 유연하게 대응 가능합니다.
- 단점
    - 반복적인 재시도는 CPU 사용률 증가와 부하를 초래할 수 있습니다.
    - 재시도 간의 대기 시간을 적절히 설정해야 합니다.

```kotlin
fun acquireLockWithSpin(key: String, ttl: Long, retryInterval: Long, maxAttempts: Int): Boolean {
    var attempts = 0
    while (attempts < maxAttempts) {
        val locked = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", Duration.ofSeconds(ttl))
        if (locked == true) {
            return true
        }
        attempts++
        Thread.sleep(retryInterval)
    }
    return false
}

```

#### 3. Pub/Sub 방식의 락 (Redisson 사용)

- Pub/Sub 기반 락은 락 획득 실패 시 대기 상태로 전환되고, 락 해제가 발생하면 알림을 통해 재시도를 트리거하는 방식입니다.

### 장점

- 분산 환경 지원
    - 여러 서버에서 자원 접근을 제어할 수 있어 클러스터 환경에서 효과적입니다.
- TTL 기반 자동 해제
    - TTL을 설정하면, 특정 시간이 지나면 자동으로 락이 해제됩니다.

### 단점

- 락의 신뢰성 문제
    - 네트워크 지연이나 Redis 장애 발생 시 락의 안정성 문제가 발생할 수 있습니다.
    - 이를 보완하기 위해 Redisson이나 ZooKeeper 같은 라이브러리를 사용할 수 있습니다.
- 추가적인 Redis 설정 필요
    - Redis 클러스터를 구성해야 하며, 데이터 손실 방지를 위해 복제 및 백업을 고려해야 합니다.

```kotlin
@Service
class RedissonLockService(private val redissonClient: RedissonClient) {

    fun executeWithLock(lockKey: String, task: () -> Unit): Boolean {
        val lock: RLock = redissonClient.getLock(lockKey)

        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                task.invoke()
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        } finally {
            lock.unlock()
        }
    }
}
```

