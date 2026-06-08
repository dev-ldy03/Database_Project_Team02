================================================================
  데이터베이스 Team02 — 대학원 박람회 상담 예약 및 운영 관리 시스템
================================================================

■ 실행 전 필수 준비
  1. JDK 11 이상 설치
  2. MySQL 8.x 실행 중 확인
  3. lib/ 폴더에 MySQL Connector/J JAR 파일 확인
     (mysql-connector-j-8.4.0.jar 또는 mysql-connector-j-9.7.0.jar)

----------------------------------------------------------------
■ DB 구축 (최초 1회 또는 초기화 시)
----------------------------------------------------------------
  MySQL Workbench에서 root 계정으로 접속 후:
    1. sql/create.sql 열기 (File → Open SQL Script)
    2. 전체 실행 (Cmd+Shift+Enter / Ctrl+Shift+Enter)

  DB 초기화가 필요할 때:
    dropdb.sql 실행 → create.sql 실행

  DB 연결 정보:
    URL      : localhost:3306
    DB 이름  : DB2026Team02
    계정     : DB2026Team02
    비밀번호 : DB2026Team02

----------------------------------------------------------------
■ 컴파일
----------------------------------------------------------------
  Mac/Linux:
    mkdir -p out
    javac -encoding UTF-8 -d out -cp "lib/*" $(find src -name "*.java")

  Windows:
    mkdir out
    javac -encoding UTF-8 -d out -cp "lib\*" src\main\Main.java src\db\*.java src\model\*.java src\dao\*.java src\service\*.java src\test\*.java src\view\*.java src\view\common\*.java src\view\student\*.java src\view\admin\*.java

----------------------------------------------------------------
■ 실행
----------------------------------------------------------------
  Mac/Linux:
    java -cp "out:lib/*" DB2026Team02.view.MainFrame

  Windows:
    java -cp "out;lib\*" DB2026Team02.view.MainFrame

================================================================
  ★★★ 관리자 로그인 비밀번호 안내 ★★★

  관리자 화면 진입 시 비밀번호 입력이 필요합니다.

  관리자 비밀번호 : ewha1886

  * 랜딩 화면에서 "관리자" 버튼 클릭 후 위 비밀번호 입력
  * 학생 화면은 비밀번호 없이 학생 등록/조회로 진입 가능
================================================================

----------------------------------------------------------------
■ 자주 발생하는 오류
----------------------------------------------------------------
  오류: No suitable driver found
  → lib/ 폴더에 MySQL Connector/J JAR 파일이 없는 것
  → https://dev.mysql.com/downloads/connector/j/ 에서 다운로드

  오류: Unknown column 'notes' 또는 'student_major'
  → DB 스키마가 구버전
  → dropdb.sql 실행 후 create.sql 재실행

  오류: DB 연결 실패
  → MySQL 서버가 꺼져 있는 것
  → Workbench에서 Local instance 3306 접속 확인

----------------------------------------------------------------
■ 진입점 정리
----------------------------------------------------------------
  전체 GUI 실행  : DB2026Team02.view.MainFrame   ← 메인 실행
  DB 연결 확인   : DB2026Team02.main.Main
  백엔드 테스트  : DB2026Team02.test.ReservationTest
                   DB2026Team02.test.StudentTest
                   DB2026Team02.test.AdminTest

  ※ 테스트 실행 후에는 create.sql로 DB를 초기화하는 것을 권장합니다.

================================================================
