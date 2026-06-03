package DB2026Team02.model;

public class ConsultationBooth {

    private int boothId;
    private int departmentId;
    private String boothName;
    private String boothType; // OFFLINE, ONLINE, HYBRID
    private int capacity;

    public ConsultationBooth() {
    }

    public ConsultationBooth(int departmentId, String boothName, String boothType, int capacity) {
        this.departmentId = departmentId;
        this.boothName = boothName;
        this.boothType = boothType;
        this.capacity = capacity;
    }

    public int getBoothId() {
        return boothId;
    }

    public void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public String getBoothType() {
        return boothType;
    }

    public void setBoothType(String boothType) {
        this.boothType = boothType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
