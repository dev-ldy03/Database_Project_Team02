# Service Package

## 역할

`service` 패키지는 애플리케이션의 비즈니스 로직을 담당한다.

DAO가 SQL 실행을 담당한다면, Service는 기능의 처리 순서와 조건 검사를 담당한다.

## 주요 클래스

### ReservationService

예약 기능의 핵심 로직을 담당

주요 기능:
- 상담 예약 생성
- 중복 예약 방지
- 상담 시간대 예약 가능 여부 확인
- 예약 취소
- 예약 ID 기반 조회
- 트랜잭션 처리

## 주요 메서드

| 메서드 | 설명 |
|---|---|
| `createReservation()` | 예약 생성, 중복 확인, 예약 가능 여부 확인, 트랜잭션 처리 |
| `cancelReservation()` | 예약 취소 처리 |
| `findReservationById()` | 예약 ID로 예약 정보 조회 |
| `isSlotAvailable()` | 특정 시간대 예약 가능 여부 확인 |
| `hasDuplicateReservation()` | 동일 학생의 동일 시간대 중복 예약 여부 확인 |

## 트랜잭션 처리

예약 생성은 하나의 트랜잭션으로 처리

처리 흐름:

```text
중복 예약 확인
→ 예약 가능 여부 확인
→ 예약 정보 저장
→ commit