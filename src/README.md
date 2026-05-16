# src

Java(JDBC) 애플리케이션 소스 코드를 보관하는 폴더입니다.

## 패키지

- **패키지명:** `DB2026Team02`
- **경로:** `src/DB2026Team02/`

## 권장 구조 (팀에서 확장)

```
src/DB2026Team02/
├── Main.java              # 프로그램 진입점
├── db/
│   └── DatabaseConnection.java   # JDBC 연결
├── model/                 # 엔티티/도메인 클래스
├── dao/                   # Data Access Object
└── service/               # 비즈니스 로직
```

## 컴파일 및 실행 (예시)

```bash
# 프로젝트 루트에서
javac -d out -cp "lib/mysql-connector-j-8.x.x.jar" src/DB2026Team02/*.java src/DB2026Team02/**/*.java
java -cp "out:lib/mysql-connector-j-8.x.x.jar" DB2026Team02.Main
```

IntelliJ/Eclipse 사용 시 `lib/`에 MySQL Connector/J JAR을 추가하고 Run Configuration을 설정하세요.

## DB 연결 정보

| 항목 | 값 |
|------|-----|
| URL | `jdbc:mysql://localhost:3306/DB2026Team02` |
| 사용자 | `DB2026Team02` |
| 비밀번호 | `DB2026Team02` |
