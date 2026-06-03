# 실행 방법 가이드 — DB2026Team02

## 사전 준비 체크리스트

- [ ] JDK 11 이상 설치
- [ ] MySQL 8.x 설치 및 실행 중
- [ ] MySQL Workbench 설치
- [ ] MySQL Connector/J JAR 파일 → `lib/` 폴더에 배치

---

## 1단계 — MySQL Connector/J 설치

> Java 코드가 MySQL DB와 연결되려면 반드시 필요한 파일이에요.

1. https://dev.mysql.com/downloads/connector/j/ 접속
2. **Platform Independent** 선택
3. **No thanks, just start my download** 클릭
4. ZIP 압축 풀기
5. `mysql-connector-j-8.x.x.jar` 파일을 프로젝트 `lib/` 폴더에 복사

```
Database_Project_Team02/
└── lib/
    └── mysql-connector-j-8.x.x.jar  ← 여기에 넣기
```

---

## 2단계 — DB 구축 (최초 1회)

> MySQL Workbench에서 진행해요.

1. Workbench 실행 → **Local instance 3306** 접속 (root 계정)
2. 상단 메뉴 **File → Open SQL Script** → `sql/create.sql` 선택
3. **Cmd + Shift + Enter** (Mac) / **Ctrl + Shift + Enter** (Windows) 로 전체 실행
4. 아래 Action Output 패널에서 결과 확인
   - ✅ 초록 체크 → 성공
   - ⚠️ 노란 경고 → 무시해도 됨
   - ❌ 빨간 오류 → 확인 필요

**DB 연결 정보**

| 항목 | 값 |
|------|-----|
| DB 이름 | `DB2026Team02` |
| 계정 | `DB2026Team02` |
| 비밀번호 | `DB2026Team02` |
| 호스트 | `localhost` |
| 포트 | `3306` |

---

## 3단계 — 컴파일

터미널(Mac) 또는 명령 프롬프트(Windows)에서 실행해요.

**Mac/Linux**
```bash
cd /path/to/Database_Project_Team02

mkdir -p out
javac -encoding UTF-8 -d out -cp "lib/*" $(find src -name "*.java")
```

**Windows**
```bat
cd C:\path\to\Database_Project_Team02

mkdir out
javac -encoding UTF-8 -d out -cp "lib\*" src\main\Main.java src\db\*.java src\model\*.java src\dao\*.java src\service\*.java src\test\*.java src\view\*.java
```

오류 없이 끝나면 `out/` 폴더에 `.class` 파일이 생성된 것 → 성공!

---

## 4단계 — 실행

**GUI 앱 실행 (메인)**

```bash
# Mac/Linux
java -cp "out:lib/*" DB2026Team02.view.MainFrame

# Windows
java -cp "out;lib\*" DB2026Team02.view.MainFrame
```

실행하면 창이 팝업으로 떠요.

> 관리자 로그인 비밀번호: `ewha1886`

---

## DB 초기화 방법

> 스키마가 바뀌거나 데이터를 다시 넣고 싶을 때

1. Workbench에서 `sql/dropdb.sql` 열고 전체 실행
2. 이어서 `sql/create.sql` 열고 전체 실행

---

## 자주 발생하는 오류

| 오류 메시지 | 원인 | 해결 방법 |
|---|---|---|
| `No suitable driver found` | lib/에 JAR 없음 | 1단계 다시 확인 |
| `Unknown column 'notes'` | DB 스키마가 구버전 | dropdb → create 재실행 |
| `연결 실패` | MySQL 서버 꺼짐 | Workbench에서 접속 확인 |
| `cannot find symbol` | 컴파일 오류 | 터미널 오류 메시지 팀에 공유 |

---

## IntelliJ 사용 시

1. **File → Open** → `Database_Project_Team02` 폴더 선택
2. `lib/*.jar` 를 프로젝트 라이브러리에 추가
   - **File → Project Structure → Libraries → + → lib 폴더 선택**
3. Run Configuration에서 Main class를 `DB2026Team02.view.MainFrame` 으로 지정
4. 실행 버튼 ▶️ 클릭
