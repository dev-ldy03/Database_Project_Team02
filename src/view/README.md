# View Package

## 역할

`view` 패키지는 Java Swing 기반의 화면 구성을 담당한다.

사용자는 SQL 문법이나 내부 프로그래밍 구조를 직접 이해하지 않아도 GUI를 통해 시스템의 주요 기능을 사용할 수 있다.

## 실행 방법

`MainFrame.java`의 main() 메서드를 실행하면 전체 화면이 시작된다.


## 패키지 구조
```
view/
├── MainFrame.java                      # 전체 화면 관리
├── common/                             # 공통 UI 컴포넌트
│   ├── NavBar.java                     
│   ├── GreenButton.java                
│   ├── StatusBadge.java                
│   └── UIConstants.java                
├── student/                            # 학생용 화면
│   ├── LandingPanel.java               # 랜딩 페이지
│   ├── StudentRegisterPanel.java       # 학생 등록 페이지
│   ├── StudentHomePanel.java           # 학생 홈 페이지
│   ├── DepartmentSearchPanel.java      # 학과 검색 페이지
│   ├── DepartmentDetailPanel.java      # 학과 상세 페이지
│   ├── MakeReservationPanel.java       # 예약 생성 페이지
│   ├── MyReservationsPanel.java        # 내 예약 페이지
│   └── ReservationDetailPanel.java     # 예약 상세 페이지
└── admin/                              # 관리자용 화면
    ├── AdminDashboardPanel.java        # 관리자 대시보드
    ├── AdminSideBar.java               # 관리자 사이드바
    ├── ReservationListPanel.java       # 예약 목록 페이지
    ├── CheckInPanel.java               # 체크인 관리 페이지
    ├── DepartmentMgmtPanel.java        # 학과 관리 페이지
    ├── ProfessorMgmtPanel.java         # 교수 관리 페이지
    └── BoothTimeSlotPanel.java         # 부스/시간대 관리 페이지
```



## 화면 전환 흐름

```
LANDING
  ├─▶ STUDENT_REGISTER ─▶ STUDENT_HOME
  │                              ├─▶ DEPT_SEARCH ─▶ DEPT_DETAIL ─▶ MAKE_RESERVATION
  │                              └─▶ MY_RESERVATIONS ─▶ RESERVATION_DETAIL
  └─▶ ADMIN_DASHBOARD
            ├─▶ ADMIN_RESERVATIONS
            ├─▶ ADMIN_CHECKIN
            ├─▶ ADMIN_DEPT
            ├─▶ ADMIN_PROF
            └─▶ ADMIN_BOOTH
```

## 참고사항

- 로그인 기능은 별도로 구현하지 않았으며 관리자 진입 비밀번호는 `ewha1886`으로 하드코딩했다.
- 관리자 비밀번호를 변경하려면 프로젝트에서 `ewha1886` 문자열을 검색하여 해당 값을 수정하면 된다.
