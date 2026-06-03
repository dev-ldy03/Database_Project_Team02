# DAO Package

## 역할

`dao` 패키지는 데이터베이스에 직접 접근하여 SQL을 실행하는 클래스들을 관리한다.

DAO는 Service 계층에서 요청한 작업을 실제 SQL문으로 처리하며, `SELECT`, `INSERT`, `UPDATE` 등의 데이터베이스 작업을 담당한다.

## 주요 클래스

### ReservationDAO : 예약 기능과 관련된 데이터베이스 작업을 담당

주요 기능:
- 중복 예약 여부 확인
- 상담 시간대 예약 가능 여부 확인
- 예약 정보 저장
- 예약 취소
- 예약 ID 기반 예약 조회

## 주요 메서드

| 메서드 | 설명 |
|---|---|
| `hasDuplicateReservation()` | 동일 학생의 동일 시간대 중복 예약 여부 확인 |
| `isSlotAvailable()` | 상담 시간대의 예약 가능 여부 확인 |
| `insertReservation()` | 예약 정보를 `RESERVATION` 테이블에 저장 |
| `cancelReservation()` | 예약 상태를 `CANCELLED`로 변경 |
| `findById()` | 예약 ID로 예약 정보 조회 |

## 트랜잭션 지원
`ReservationDAO`는 일반 실행용 메서드와 트랜잭션용 메서드를 함께 제공
트랜잭션용 메서드는 `Connection` 객체를 외부에서 전달받아 하나의 트랜잭션 안에서 여러 SQL 작업을 처리할 수 있도록 처리
예시:
```java
hasDuplicateReservation(Connection conn, int studentId, int slotId)
isSlotAvailable(Connection conn, int slotId)
insertReservation(Connection conn, Reservation reservation)