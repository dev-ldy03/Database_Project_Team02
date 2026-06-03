# DB2026Team02 — 대학원 박람회 상담 예약 및 운영 관리 시스템

이화여자대학교 **데이터베이스** 수업 팀 프로젝트 (팀명: **DB2026Team02**)

대학원 박람회에서 학과·교수 상담 부스의 **시간대 예약**, **체크인/체크아웃**, **노쇼 처리**, **온라인 링크** 관리를 MySQL과 Java(JDBC)로 구현합니다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| DB | MySQL 8.x |
| 애플리케이션 | Java (JDBC), 3-tier (Model · DAO · Service) |
| 패키지명 | `DB2026Team02` |

## 시스템 구성

```
[콘솔 테스트 / 향후 view] → Service → DAO → MySQL (DB2026Team02)
```

| 역할 | Service | 주요 기능 |
|------|---------|-----------|
| **학생** | `StudentService` | 학생 등록·조회, 학과/교수/부스/시간대 조회, 내 예약 목록 |
| **예약** | `ReservationService` | 예약 생성(중복·정원 검사, 트랜잭션), 취소, 조회 |
| **운영(관리자)** | `AdminService` | 학과·교수·부스·시간대 CRUD, 예약·체크인·노쇼, 통계 |

UI(`src/view/`)는 프론트 담당 영역이며, 백엔드 검증은 `src/test/` 콘솔 테스트로 수행합니다.

## 폴더 구조

```
Database_Project_Team02/
├── README.md
├── .gitignore
├── sql/
│   ├── README.md
│   ├── create.sql            # DB/계정, 테이블, 인덱스, 뷰, 샘플 데이터
│   └── dropdb.sql            # 테이블·DB 삭제
├── src/
│   ├── README.md
│   ├── main/
│   │   └── Main.java                 # 진입점 (DB 연결 확인)
│   ├── db/
│   │   └── DatabaseConnection.java   # JDBC 연결
│   ├── model/                        # 엔티티 (Student, Reservation, …)
│   ├── dao/
│   │   ├── ReservationDAO.java
│   │   ├── StudentDAO.java
│   │   └── AdminDAO.java
│   ├── service/
│   │   ├── ReservationService.java
│   │   ├── StudentService.java
│   │   └── AdminService.java
│   ├── test/                         # 콘솔 기능 테스트
│   │   ├── ReservationTest.java
│   │   ├── StudentTest.java
│   │   └── AdminTest.java
│   └── view/                         # UI·화면 (프론트)
├── lib/                              # MySQL Connector/J JAR
│   └── README.md
└── docs/
    ├── README.md
    ├── report/
    ├── presentation/
    └── demo/
```

하위 폴더별 상세 설명은 각 디렉터리의 `README.md`를 참고하세요.

## DB 스키마

### 테이블

| 테이블 | 설명 |
|--------|------|
| `DEPARTMENT` | 학과 |
| `PROFESSOR` | 상담 교수 |
| `CONSULTATION_BOOTH` | 상담 부스 (`OFFLINE` / `ONLINE` / `HYBRID`) |
| `TIME_SLOT` | 부스별 상담 시간대 |
| `STUDENT` | 예약 학생 |
| `RESERVATION` | 상담 예약 (`PENDING` / `CONFIRMED` / `CANCELLED` / `COMPLETED`) |
| `CHECK_IN_RECORD` | 현장 체크인·체크아웃 |
| `ONLINE_LINK` | 온라인 상담 회의 링크 |

### 뷰 (`create.sql`)

| 뷰 | 설명 |
|----|------|
| `v_reservation_detail` | 예약 + 학생·학과·교수·부스·시간대 통합 조회 |
| `v_booth_schedule` | 부스별 일정 및 현재 예약 건수 |

### 샘플 데이터

`create.sql` 실행 시 학과·교수·부스·시간대·학생·예약 등 초기 데이터가 함께 적재됩니다. 테스트 반복 시 `dropdb.sql` 후 `create.sql`을 다시 실행하는 것을 권장합니다.

## 실행 방법

### 1. 사전 요구사항

- MySQL 8.x (또는 수업 환경 버전)
- JDK 11 이상
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) JAR → `lib/`에 배치 (`lib/README.md` 참고)

### 2. 데이터베이스 구축

```bash
# 프로젝트 루트에서
mysql -u root -p < sql/create.sql
```

| 항목 | 값 |
|------|-----|
| DB 이름 | `DB2026Team02` |
| 계정 / 비밀번호 | `DB2026Team02` / `DB2026Team02` |
| 호스트 / 포트 | `localhost` / `3306` |

MySQL Workbench 사용 시: root로 접속 → `create.sql` 전체 실행.

테이블만 초기화할 때:

```bash
mysql -u root -p < sql/dropdb.sql
mysql -u root -p < sql/create.sql
```

### 3. Java 컴파일 및 실행

`lib/`에 Connector/J JAR 파일명을 맞춘 뒤, 프로젝트 루트에서 실행합니다.

```bash
# 컴파일 (macOS/Linux)
mkdir -p out
javac -encoding UTF-8 -d out -cp "lib/*" $(find src -name "*.java")

# 메인 (DB 연결 확인)
java -cp "out:lib/*" DB2026Team02.main.Main
```

Windows(cmd) 예시:

```bat
mkdir out
javac -encoding UTF-8 -d out -cp "lib\*" src\main\Main.java src\db\*.java src\model\*.java src\dao\*.java src\service\*.java src\test\*.java
java -cp "out;lib\*" DB2026Team02.main.Main
```

IntelliJ / Eclipse 사용 시: `lib/` JAR를 라이브러리로 추가하고, 실행 클래스의 Main class를 지정하세요.

### 4. 백엔드 기능 테스트 (콘솔)

DB를 `create.sql`로 준비한 뒤, 아래 클래스를 각각 실행합니다.

| 클래스 | 검증 대상 |
|--------|-----------|
| `DB2026Team02.test.ReservationTest` | 예약 생성·취소·중복 방지·정원 |
| `DB2026Team02.test.StudentTest` | 학생 등록·조회·학과/시간대 조회 |
| `DB2026Team02.test.AdminTest` | 학과·부스·시간대·체크인·통계 등 |

```bash
java -cp "out:lib/*" DB2026Team02.test.ReservationTest
java -cp "out:lib/*" DB2026Team02.test.StudentTest
java -cp "out:lib/*" DB2026Team02.test.AdminTest
```

테스트는 실제 DB 데이터를 변경합니다. 재실행 전 `create.sql`로 DB를 초기화하세요.

## 제출물

| 항목 | 위치 |
|------|------|
| `create.sql` (뷰 2+, 인덱스 4+) | `sql/create.sql` |
| `dropdb.sql` | `sql/dropdb.sql` |
| Java 소스 | `src/` (`DB2026Team02` 패키지) |
| 레포트 | `docs/report/` |
| 발표 PPT | `docs/presentation/` |
| 시연 영상 | `docs/demo/` |

## 라이선스 / 저작

수업 제출용 프로젝트입니다. 외부 배포 시 팀·교수님 안내를 따릅니다.
