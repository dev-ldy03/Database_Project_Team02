package DB2026Team02.model;

import java.time.LocalDateTime;

public class Reservation {

    private int reservationId;
    private int studentId;
    private int slotId;
    private int professorId;
    private int boothId;

    private String status;
    private LocalDateTime createdAt;
    private String notes;

    public Reservation() {
    }

    public Reservation(int studentId, int slotId,
                       int professorId, int boothId,
                       String status, String notes) {

        this.studentId = studentId;
        this.slotId = slotId;
        this.professorId = professorId;
        this.boothId = boothId;
        this.status = status;
        this.notes = notes;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public int getBoothId() {
        return boothId;
    }

    public void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}