-- =============================================================================
-- DB2026Team02: 대학원 박람회 상담 예약 및 운영 관리 시스템
-- create.sql - DB/계정 생성, 테이블, 인덱스(4+), 뷰(2+), 초기 데이터
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 데이터베이스 및 계정
-- -----------------------------------------------------------------------------
-- 기존 데이터베이스 및 계정 삭제
DROP DATABASE IF EXISTS DB2026Team02;
DROP USER IF EXISTS 'DB2026Team02'@'localhost';
DROP USER IF EXISTS 'DB2026Team02'@'%';


CREATE DATABASE IF NOT EXISTS DB2026Team02
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'DB2026Team02'@'localhost' IDENTIFIED BY 'DB2026Team02';
CREATE USER IF NOT EXISTS 'DB2026Team02'@'%' IDENTIFIED BY 'DB2026Team02';

GRANT ALL PRIVILEGES ON DB2026Team02.* TO 'DB2026Team02'@'localhost';
GRANT ALL PRIVILEGES ON DB2026Team02.* TO 'DB2026Team02'@'%';
FLUSH PRIVILEGES;

USE DB2026Team02;

-- -----------------------------------------------------------------------------
-- 2. 테이블 생성 (FK 참조 순서)
-- -----------------------------------------------------------------------------

-- 2-1. DEPARTMENT
CREATE TABLE DEPARTMENT (
    department_id   INT             NOT NULL AUTO_INCREMENT,
    department_name VARCHAR(100)    NOT NULL,
    location        VARCHAR(200)    NULL,
    PRIMARY KEY (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-2. PROFESSOR (→ DEPARTMENT)
CREATE TABLE PROFESSOR (
    professor_id    INT             NOT NULL AUTO_INCREMENT,
    department_id   INT             NOT NULL,
    professor_name  VARCHAR(50)     NOT NULL,
    email           VARCHAR(100)    NULL,
    phone           VARCHAR(20)     NULL,
    PRIMARY KEY (professor_id),
    CONSTRAINT fk_professor_department
        FOREIGN KEY (department_id) REFERENCES DEPARTMENT (department_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-3. CONSULTATION_BOOTH (→ DEPARTMENT)
CREATE TABLE CONSULTATION_BOOTH (
    booth_id        INT             NOT NULL AUTO_INCREMENT,
    department_id   INT             NOT NULL,
    booth_name      VARCHAR(100)    NOT NULL,
    booth_type      ENUM('OFFLINE', 'ONLINE', 'HYBRID') NOT NULL DEFAULT 'OFFLINE',
    capacity        INT             NOT NULL DEFAULT 1,
    PRIMARY KEY (booth_id),
    CONSTRAINT fk_booth_department
        FOREIGN KEY (department_id) REFERENCES DEPARTMENT (department_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-4. TIME_SLOT (→ CONSULTATION_BOOTH)
CREATE TABLE TIME_SLOT (
    slot_id         INT             NOT NULL AUTO_INCREMENT,
    booth_id        INT             NOT NULL,
    slot_date       DATE            NOT NULL,
    start_time      TIME            NOT NULL,
    end_time        TIME            NOT NULL,
    max_reservations INT            NOT NULL DEFAULT 1,
    PRIMARY KEY (slot_id),
    CONSTRAINT fk_timeslot_booth
        FOREIGN KEY (booth_id) REFERENCES CONSULTATION_BOOTH (booth_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_timeslot_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-5. STUDENT
CREATE TABLE STUDENT (
    student_id      INT             NOT NULL AUTO_INCREMENT,
    student_name    VARCHAR(50)     NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    phone           VARCHAR(20)     NULL,
    major           VARCHAR(100)    NULL,
    PRIMARY KEY (student_id),
    UNIQUE KEY uk_student_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-6. RESERVATION (→ STUDENT, TIME_SLOT, PROFESSOR, CONSULTATION_BOOTH)
CREATE TABLE RESERVATION (
    reservation_id  INT             NOT NULL AUTO_INCREMENT,
    student_id      INT             NOT NULL,
    slot_id         INT             NOT NULL,
    professor_id    INT             NOT NULL,
    booth_id        INT             NOT NULL,
    status          ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes           VARCHAR(500)    NULL,
    PRIMARY KEY (reservation_id),
    CONSTRAINT fk_reservation_student
        FOREIGN KEY (student_id) REFERENCES STUDENT (student_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_slot
        FOREIGN KEY (slot_id) REFERENCES TIME_SLOT (slot_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_professor
        FOREIGN KEY (professor_id) REFERENCES PROFESSOR (professor_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_booth
        FOREIGN KEY (booth_id) REFERENCES CONSULTATION_BOOTH (booth_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-7. CHECK_IN_RECORD (→ RESERVATION)
CREATE TABLE CHECK_IN_RECORD (
    check_in_id     INT             NOT NULL AUTO_INCREMENT,
    reservation_id  INT             NOT NULL,
    check_in_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    check_out_time  DATETIME        NULL,
    PRIMARY KEY (check_in_id),
    CONSTRAINT fk_checkin_reservation
        FOREIGN KEY (reservation_id) REFERENCES RESERVATION (reservation_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2-8. ONLINE_LINK (→ RESERVATION)
CREATE TABLE ONLINE_LINK (
    link_id         INT             NOT NULL AUTO_INCREMENT,
    reservation_id  INT             NOT NULL,
    meeting_url     VARCHAR(500)    NOT NULL,
    meeting_password VARCHAR(50)    NULL,
    expires_at      DATETIME        NULL,
    PRIMARY KEY (link_id),
    CONSTRAINT fk_onlinelink_reservation
        FOREIGN KEY (reservation_id) REFERENCES RESERVATION (reservation_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3. 인덱스 (4개 이상)
-- -----------------------------------------------------------------------------
CREATE INDEX idx_professor_department ON PROFESSOR (department_id);
CREATE INDEX idx_booth_department ON CONSULTATION_BOOTH (department_id);
CREATE INDEX idx_timeslot_booth_date ON TIME_SLOT (booth_id, slot_date);
CREATE INDEX idx_reservation_student ON RESERVATION (student_id);
CREATE INDEX idx_reservation_slot ON RESERVATION (slot_id);
CREATE INDEX idx_reservation_status ON RESERVATION (status);
CREATE INDEX idx_checkin_reservation ON CHECK_IN_RECORD (reservation_id);

-- -----------------------------------------------------------------------------
-- 4. 뷰 (2개 이상)
-- -----------------------------------------------------------------------------

-- 예약 상세 (학과·교수·부스·시간대 정보 포함)
CREATE OR REPLACE VIEW v_reservation_detail AS
SELECT
    r.reservation_id,
    r.status,
    r.created_at,
    s.student_id,
    s.student_name,
    s.email           AS student_email,
    d.department_name,
    p.professor_name,
    b.booth_name,
    b.booth_type,
    ts.slot_date,
    ts.start_time,
    ts.end_time
FROM RESERVATION r
JOIN STUDENT s ON r.student_id = s.student_id
JOIN PROFESSOR p ON r.professor_id = p.professor_id
JOIN DEPARTMENT d ON p.department_id = d.department_id
JOIN CONSULTATION_BOOTH b ON r.booth_id = b.booth_id
JOIN TIME_SLOT ts ON r.slot_id = ts.slot_id;

-- 부스별 일정 및 예약 현황
CREATE OR REPLACE VIEW v_booth_schedule AS
SELECT
    b.booth_id,
    b.booth_name,
    d.department_name,
    ts.slot_id,
    ts.slot_date,
    ts.start_time,
    ts.end_time,
    ts.max_reservations,
    COUNT(r.reservation_id) AS current_reservations
FROM CONSULTATION_BOOTH b
JOIN DEPARTMENT d ON b.department_id = d.department_id
JOIN TIME_SLOT ts ON b.booth_id = ts.booth_id
LEFT JOIN RESERVATION r ON ts.slot_id = r.slot_id AND r.status IN ('PENDING', 'CONFIRMED')
GROUP BY b.booth_id, b.booth_name, d.department_name,
         ts.slot_id, ts.slot_date, ts.start_time, ts.end_time, ts.max_reservations;

-- -----------------------------------------------------------------------------
-- 5. 초기 데이터 (샘플 )
-- -----------------------------------------------------------------------------
-- 5건
INSERT INTO DEPARTMENT (department_name, location) VALUES
    ('컴퓨터공학과', '공학관 A동'),
    ('경영학과', '경영관 B동'),
    ('생명과학과', '생명관 C동'),
    ('국제학부', '국제관 D동'),
    ('교육학과', '사범관 E동');

-- PROFESSOR 각 학과별 1~2명씩 (총 7건)
INSERT INTO PROFESSOR (department_id, professor_name, email) VALUES
    (1, '김교수', 'prof.kim@ewha.ac.kr'),
    (1, '박교수', 'prof.park@ewha.ac.kr'),
    (2, '이교수', 'prof.lee@ewha.ac.kr'),
    (3, '최교수', 'prof.choi@ewha.ac.kr'),
    (4, '정교수', 'prof.jung@ewha.ac.kr'),
    (5, '한교수', 'prof.han@ewha.ac.kr'),
    (5, '윤교수', 'prof.yoon@ewha.ac.kr');

-- CONSULTATION_BOOTH 5건 (학과별 1개, 부스 유형 다양화)
INSERT INTO CONSULTATION_BOOTH (department_id, booth_name, booth_type, capacity) VALUES
    (1, '컴퓨터공학 온라인 부스', 'ONLINE', 3),
    (2, '경영학 오프라인 부스', 'OFFLINE', 5),
    (3, '생명과학 하이브리드 부스', 'HYBRID', 4),
    (4, '국제학부 온라인 부스', 'ONLINE', 2),
    (5, '교육학 오프라인 부스', 'OFFLINE', 3);

-- TIME_SLOT 5건 (부스별 상담 시간대)
INSERT INTO TIME_SLOT (booth_id, slot_date, start_time, end_time, max_reservations) VALUES
    (1, '2026-05-20', '10:00:00', '10:30:00', 3),
    (2, '2026-05-20', '14:00:00', '14:30:00', 5),
    (3, '2026-05-20', '11:00:00', '11:30:00', 4),
    (4, '2026-05-20', '15:00:00', '15:30:00', 2),
    (5, '2026-05-20', '09:00:00', '09:30:00', 3);

-- STUDENT 5건
INSERT INTO STUDENT (student_name, email, phone, major) VALUES
    ('홍길동', 'student1@ewha.ac.kr', '010-0000-0001', '컴퓨터공학'),
    ('김영희', 'student2@ewha.ac.kr', '010-0000-0002', '경영학'),
    ('이민준', 'student3@ewha.ac.kr', '010-0000-0003', '생명과학'),
    ('박서연', 'student4@ewha.ac.kr', '010-0000-0004', '국제학'),
    ('최지우', 'student5@ewha.ac.kr', '010-0000-0005', '교육학');

-- RESERVATION 5건 (STUDENT, TIME_SLOT, PROFESSOR, BOOTH 다 있어야 함)
INSERT INTO RESERVATION (student_id, slot_id, professor_id, booth_id, status) VALUES
    (1, 1, 1, 1, 'CONFIRMED'),
    (2, 2, 3, 2, 'CONFIRMED'),
    (3, 1, 2, 1, 'PENDING'),
    (4, 2, 4, 2, 'CANCELLED'),
    (5, 1, 1, 1, 'COMPLETED');

-- CHECK_IN_RECORD (COMPLETED 상태인 예약에 대해)
INSERT INTO CHECK_IN_RECORD (reservation_id, check_in_time, check_out_time) VALUES
    (5, '2026-05-20 10:00:00', '2026-05-20 10:30:00');

-- ONLINE_LINK (ONLINE 부스 예약에 대해)
INSERT INTO ONLINE_LINK (reservation_id, meeting_url, meeting_password, expires_at) VALUES
    (1, 'https://zoom.us/j/123456789', '1234', '2026-05-20 11:00:00'),
    (3, 'https://zoom.us/j/987654321', '5678', '2026-05-20 11:00:00');
