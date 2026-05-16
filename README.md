# DB2026Team02 — 대학원 박람회 상담 예약 및 운영 관리 시스템

이화여자대학교 **데이터베이스** 수업 팀 프로젝트 (팀명: **DB2026Team02**)

대학원 박람회에서 학과·교수 상담 부스의 **시간대 예약**, **체크인**, **온라인 링크** 관리를 MySQL과 Java(JDBC)로 구현합니다.



## 기술 스택

- **DB:** MySQL
- **애플리케이션:** Java (JDBC)


## 폴더 구조

```
Database_Project_Team02/
├── README.md                 # 프로젝트 소개 (본 파일)
├── .gitignore
├── sql/
│   ├── README.md
│   ├── create.sql            # 테이블, 인덱스, 뷰, 초기 데이터
│   └── dropdb.sql            # 테이블 삭제
├── src/
│   ├── README.md
│   ├── main/
│   │   └── Main.java         # 프로그램 진입점
│   ├── db/
│   │   └── DatabaseConnection.java
│   ├── dao/                  # Data Access Object
│   ├── model/                # 엔티티/도메인 클래스
│   └── view/                 # UI·화면
├── lib/                      # MySQL Connector/J 등 JAR
│   └── README.md
└── docs/
    ├── README.md
    ├── report/               # 레포트
    │   └── README.md
    ├── presentation/         # 발표 PPT
    │   └── README.md
    └── demo/                 # 시연 동영상
        └── README.md
```

각 폴더의 역할은 해당 디렉터리의 `README.md`를 참고하세요.

## DB 스키마 (테이블)

| 테이블 | 설명 |
|--------|------|
| `DEPARTMENT` | 학과 |
| `PROFESSOR` | 상담 교수 |
| `CONSULTATION_BOOTH` | 상담 부스 |
| `TIME_SLOT` | 부스별 상담 시간대 |
| `STUDENT` | 예약 학생 |
| `RESERVATION` | 상담 예약 |
| `CHECK_IN_RECORD` | 현장 체크인 |
| `ONLINE_LINK` | 온라인 상담 링크 |

## 실행 방법

### 1. 사전 요구사항

- MySQL 8.x (또는 수업 환경 버전)
- JDK 11 이상 (팀에서 사용하는 버전으로 통일)
- MySQL Connector/J → `lib/` 참고

### 2. 데이터베이스 구축

```bash
# 프로젝트 루트에서
mysql -u root -p < sql/create.sql
```

- **DB 이름:** `DB2026Team02`
- **계정 / 비밀번호:** `DB2026Team02` / `DB2026Team02`

초기화(테이블만 삭제):

```bash
mysql -u root -p < sql/dropdb.sql
```

### 3. Java 애플리케이션 실행



## 제출물

| 항목 | 위치 |
|------|------|
| `create.sql` (뷰 2+, 인덱스 4+) | `sql/create.sql` |
| `dropdb.sql` | `sql/dropdb.sql` |
| Java 소스 | `src/DB2026Team02/` |
| 레포트 | `docs/report/` |
| 발표 PPT | `docs/presentation/` |
| 시연 영상 | `docs/demo/` |

## 라이선스 / 저작

수업 제출용 프로젝트입니다. 외부 배포 시 팀·교수님 안내를 따릅니다.
