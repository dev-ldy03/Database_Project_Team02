# Model Package

## 역할

`model` 패키지는 데이터베이스 테이블의 데이터를 Java 객체로 표현하는 클래스들을 관리한다.

Model 클래스는 SQL을 실행하지 않고, 데이터를 저장하고 전달하는 역할만 한다.

## 주요 클래스
### Student

`STUDENT` 테이블의 한 행(row)을 Java 객체로 표현한다.

주요 필드:
- `studentId`
- `studentName`
- `email`
- `phone`
- `major`

---

### TimeSlot

`TIME_SLOT` 테이블의 한 행(row)을 Java 객체로 표현한다.

주요 필드:
- `slotId`
- `boothId`
- `slotDate`
- `startTime`
- `endTime`
- `maxReservations`

---

### Reservation

`RESERVATION` 테이블의 한 행(row)을 Java 객체로 표현한다.

주요 필드:
- `reservationId`
- `studentId`
- `slotId`
- `professorId`
- `boothId`
- `status`
- `createdAt`
- `notes`

## 특징

- 데이터 저장 및 전달용 클래스
- getter/setter로 데이터 접근
- SQL 실행 로직 없음
- DAO, Service 계층에서 공통으로 사용