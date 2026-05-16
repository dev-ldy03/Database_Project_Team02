# sql

MySQL 데이터베이스 스키마 및 초기 데이터 스크립트를 보관하는 폴더입니다.

## 파일

| 파일 | 설명 |
|------|------|
| `create.sql` | DB/계정 생성, 테이블 생성, 인덱스, 뷰, 초기 데이터 삽입 |
| `dropdb.sql` | 테이블 및 DB 삭제 (FK 역순) |

## 실행 순서

1. MySQL 서버 실행
2. `create.sql` 실행 → 스키마 및 샘플 데이터 구축
3. 개발·시연 후 초기화가 필요하면 `dropdb.sql` 실행

```bash
mysql -u root -p < sql/create.sql
mysql -u root -p < sql/dropdb.sql
```

## 테이블 (생성 순서)

`DEPARTMENT` → `PROFESSOR`, `CONSULTATION_BOOTH` → `TIME_SLOT` → `STUDENT` → `RESERVATION` → `CHECK_IN_RECORD`, `ONLINE_LINK`
