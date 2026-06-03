package DB2026Team02.model;

import java.sql.Date;
import java.sql.Time;

public class TimeSlot {

    private int slotId;
    private int boothId;

    private Date slotDate;
    private Time startTime;
    private Time endTime;

    private int maxReservations;

    public TimeSlot() {
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getBoothId() {
        return boothId;
    }

    public void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    public Date getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(Date slotDate) {
        this.slotDate = slotDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getMaxReservations() {
        return maxReservations;
    }

    public void setMaxReservations(int maxReservations) {
        this.maxReservations = maxReservations;
    }
}