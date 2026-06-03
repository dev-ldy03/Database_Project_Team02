package DB2026Team02.model;

import java.time.LocalDateTime;

public class CheckInRecord {

    private int checkInId;
    private int reservationId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public CheckInRecord() {
    }

    public CheckInRecord(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(int checkInId) {
        this.checkInId = checkInId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
