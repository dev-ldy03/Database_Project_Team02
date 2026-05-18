-- =============================================================================
-- DB2026Team02: 대학원 박람회 상담 예약 및 운영 관리 시스템
-- dropdb.sql - 테이블·뷰·DB 삭제 (FK 역순)
-- =============================================================================

USE DB2026Team02;

-- -----------------------------------------------------------------------------
-- 1. 뷰 삭제
-- -----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_booth_schedule;
DROP VIEW IF EXISTS v_reservation_detail;

-- -----------------------------------------------------------------------------
-- 2. 테이블 삭제 (FK 참조 역순)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS ONLINE_LINK;
DROP TABLE IF EXISTS CHECK_IN_RECORD;
DROP TABLE IF EXISTS RESERVATION;
DROP TABLE IF EXISTS STUDENT;
DROP TABLE IF EXISTS TIME_SLOT;
DROP TABLE IF EXISTS CONSULTATION_BOOTH;
DROP TABLE IF EXISTS PROFESSOR;
DROP TABLE IF EXISTS DEPARTMENT;

-- -----------------------------------------------------------------------------
-- 3. 데이터베이스 및 계정 삭제 
--    전체 초기화 시 아래 주석 해제
--    필요한 경우만 주석 해제
-- -----------------------------------------------------------------------------
DROP DATABASE IF EXISTS DB2026Team02;
DROP USER IF EXISTS 'DB2026Team02'@'localhost';
DROP USER IF EXISTS 'DB2026Team02'@'%';
FLUSH PRIVILEGES;
